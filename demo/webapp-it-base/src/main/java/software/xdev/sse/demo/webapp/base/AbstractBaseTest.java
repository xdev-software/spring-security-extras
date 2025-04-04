package software.xdev.sse.demo.webapp.base;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;

import software.xdev.sse.demo.tci.db.DBTCI;
import software.xdev.sse.demo.tci.db.factory.DBTCIFactory;
import software.xdev.sse.demo.tci.oidc.OIDCTCI;
import software.xdev.sse.demo.tci.oidc.factory.OIDCTCIFactory;
import software.xdev.sse.demo.tci.selenium.BrowserTCI;
import software.xdev.sse.demo.tci.selenium.TestBrowser;
import software.xdev.sse.demo.tci.selenium.factory.BrowsersTCIFactory;
import software.xdev.sse.demo.tci.selenium.testbase.SeleniumIntegrationTestExtension;
import software.xdev.sse.demo.tci.util.ContainerLoggingUtil;
import software.xdev.sse.demo.tci.webapp.WebAppTCI;
import software.xdev.tci.TCI;
import software.xdev.tci.factory.prestart.PreStartableTCIFactory;
import software.xdev.tci.factory.registry.TCIFactoryRegistry;
import software.xdev.tci.leakdetection.LeakDetectionAsyncReaper;
import software.xdev.tci.network.LazyNetworkPool;
import software.xdev.tci.tracing.TCITracer;


@SuppressWarnings({"java:S1117", "java:S112"})
@ExtendWith(AbstractBaseTest.TCSTSeleniumIntegrationTestExtension.class)
public abstract class AbstractBaseTest<T extends WebAppTCI<?>> implements IntegrationTestDefaults<AbstractBaseTest<T>>
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractBaseTest.class);
	
	static final Set<CompletableFuture<?>> REAP_CFS =
		Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
	
	private static final TCITracer.Timed TRACE_START_BASE_INFRA = new TCITracer.Timed();
	private static final TCITracer.Timed TRACE_START_WEB_DRIVER = new TCITracer.Timed();
	
	private Network network;
	
	protected static final String DNS_NAME_DB = "db";
	protected static final String DNS_NAME_OIDC = "oidc";
	protected static final String DNS_NAME_WEBAPP = "webapp";
	
	protected static final DBTCIFactory DB_INFRA_FACTORY = new DBTCIFactory();
	protected static final OIDCTCIFactory OIDC_INFRA_FACTORY = new OIDCTCIFactory();
	protected static final BrowsersTCIFactory BROWSER_INFRA_FACTORY = new BrowsersTCIFactory();
	protected static final LazyNetworkPool LAZY_NETWORK_POOL = new LazyNetworkPool();
	
	protected final PreStartableTCIFactory<?, T> appInfraFactory;
	
	private DBTCI dbInfra;
	private OIDCTCI oidcInfra;
	private T appInfra;
	private BrowserTCI browserInfra;
	
	private RemoteWebDriver remoteWebDriver;
	
	protected AbstractBaseTest(final PreStartableTCIFactory<?, T> appInfraFactory)
	{
		this.appInfraFactory = appInfraFactory;
	}
	
	@BeforeAll
	public static void setup()
	{
		ContainerLoggingUtil.redirectJULtoSLF4J();
		
		LAZY_NETWORK_POOL.managePoolAsync();
		
		TCIFactoryRegistry.instance().warmUp();
	}
	
	public void startAll(final TestBrowser testBrowser)
	{
		this.startAll(testBrowser, null);
	}
	
	public void startAll(final TestBrowser testBrowser, final Consumer<DBTCI> onDataBaseMigrated)
	{
		this.startBaseInfrastructure(onDataBaseMigrated);
		this.startWebDriver(testBrowser);
	}
	
	protected void startBaseInfrastructure(final Consumer<DBTCI> onDataBaseMigrated)
	{
		final long start = System.currentTimeMillis();
		
		CompletableFuture<OIDCTCI> cfOIDC = null;
		CompletableFuture<T> cfApp = null;
		try
		{
			this.network = LAZY_NETWORK_POOL.getNew();
			
			cfOIDC = CompletableFuture.supplyAsync(
				() -> OIDC_INFRA_FACTORY.getNew(this.network, DNS_NAME_OIDC));
			
			cfApp = CompletableFuture.supplyAsync(
				() -> this.appInfraFactory.getNew(this.network, DNS_NAME_WEBAPP));
			
			this.dbInfra = DB_INFRA_FACTORY.getNew(this.network, DNS_NAME_DB);
			Optional.ofNullable(onDataBaseMigrated).ifPresent(c -> c.accept(this.dbInfra));
			
			LOG.info(">>> User: {}", DBTCI.DB_USERNAME);
			LOG.info(">>> Password: {}", DBTCI.DB_PASSWORD);
			LOG.info(">>> JDBC (external): {}", this.dbInfra.getExternalJDBCUrl());
			
			this.oidcInfra = cfOIDC.join();
			LOG.info(">>> OIDC Server: {}", this.oidcInfra.getExternalHttpBaseEndPoint());
			
			this.appInfra = cfApp.join();
			LOG.info(">>> HTTP Interface (external): {}", this.appInfra.getExternalHTTPEndpoint());
		}
		catch(final Exception ex)
		{
			// Ensure that we do not leak when e.g. DB migration fails
			this.ensureDestroyAsyncStartingInfra(() -> this.oidcInfra, i -> this.oidcInfra = i, cfOIDC);
			this.ensureDestroyAsyncStartingInfra(() -> this.appInfra, i -> this.appInfra = i, cfApp);
			
			throw new RuntimeException("Failed to setup base infrastructure", ex);
		}
		TRACE_START_BASE_INFRA.addMs(System.currentTimeMillis() - start);
	}
	
	protected <X extends TCI<?>> void ensureDestroyAsyncStartingInfra(
		final Supplier<X> getCurrentInfra,
		final Consumer<X> setCurrentInfra,
		final CompletableFuture<X> cfStartingInfra)
	{
		if(getCurrentInfra.get() != null // Infra was already started and set
			|| cfStartingInfra == null) // Infra was never started
		{
			return;
		}
		try
		{
			// Try to get starting infra and set it, so it can be properly destroyed
			setCurrentInfra.accept(cfStartingInfra.get(3, TimeUnit.MINUTES));
		}
		catch(final InterruptedException iex)
		{
			LOG.warn("Got interrupted", iex);
			Thread.currentThread().interrupt();
		}
		catch(final Exception ex)
		{
			LOG.warn("Failed to get starting infra in time", ex);
		}
	}
	
	public void startWebDriver(final TestBrowser testBrowser)
	{
		final long start = System.currentTimeMillis();
		try
		{
			this.browserInfra = BROWSER_INFRA_FACTORY.getNew(testBrowser.getCapabilityFactory().get(), this.network);
			this.browserInfra.getVncAddress().ifPresent(a -> LOG.info(">>> VNC: {}", a));
			this.browserInfra.getNoVncAddress().ifPresent(a -> LOG.info(">>> NoVNC: {}", a));
			
			this.remoteWebDriver = this.browserInfra.getWebDriver();
			LOG.debug("Got WebDriver");
		}
		catch(final Exception ex)
		{
			throw new RuntimeException("Failed to setup webDriver", ex);
		}
		TRACE_START_WEB_DRIVER.addMs(System.currentTimeMillis() - start);
	}
	
	protected void stopWebDriver()
	{
		if(this.browserInfra == null)
		{
			return;
		}
		
		final RemoteWebDriver remoteWebDriver = this.remoteWebDriver;
		final BrowserTCI browserInfra = this.browserInfra;
		
		REAP_CFS.add(CompletableFuture.runAsync(() -> {
			try
			{
				if(remoteWebDriver != null && remoteWebDriver.getSessionId() != null)
				{
					LOG.info("Quiting remoteWebDriver");
					remoteWebDriver.quit();
				}
				
				browserInfra.stop();
			}
			catch(final Exception ex)
			{
				LOG.warn("Failed to stop WebDriver(async)", ex);
			}
		}));
		
		this.remoteWebDriver = null;
		this.browserInfra = null;
	}
	
	protected void stopEverything()
	{
		LOG.info("Shutting down");
		
		final T appInfra = this.appInfra;
		final OIDCTCI oidcInfra = this.oidcInfra;
		final DBTCI dbInfra = this.dbInfra;
		
		final Network network = this.network;
		
		REAP_CFS.add(CompletableFuture.runAsync(() -> {
			try
			{
				Stream.<Runnable>concat(
						Stream.of(this::stopWebDriver),
						Stream.of(appInfra, oidcInfra, dbInfra)
							.filter(Objects::nonNull)
							.map(tci -> tci::stop))
					.map(CompletableFuture::runAsync)
					.toList() // collect so everything is getting executed async
					.forEach(CompletableFuture::join);
				
				Optional.ofNullable(network).ifPresent(Network::close);
			}
			catch(final Exception ex)
			{
				LOG.error("Failed to stop everything(async)", ex);
			}
		}));
		
		this.appInfra = null;
		this.oidcInfra = null;
		this.dbInfra = null;
		
		this.network = null;
	}
	
	public WebDriver getWebDriver()
	{
		return this.remoteWebDriver;
	}
	
	public Network network()
	{
		return this.network;
	}
	
	public DBTCI dbInfra()
	{
		return this.dbInfra;
	}
	
	public OIDCTCI oidcInfra()
	{
		return this.oidcInfra;
	}
	
	public T appInfra()
	{
		return this.appInfra;
	}
	
	public BrowserTCI browserInfra()
	{
		return this.browserInfra;
	}
	
	public String getWebAppBaseUrl()
	{
		return this.appInfra.getInternalHTTPEndpoint();
	}
	
	public static class TCSTSeleniumIntegrationTestExtension
		extends SeleniumIntegrationTestExtension
		implements BeforeTestExecutionCallback
	{
		private static final Logger LOG = LoggerFactory.getLogger(TCSTSeleniumIntegrationTestExtension.class);
		
		public TCSTSeleniumIntegrationTestExtension()
		{
			super(context -> context.getTestInstance()
				.filter(AbstractBaseTest.class::isInstance)
				.map(AbstractBaseTest.class::cast)
				.map(AbstractBaseTest::browserInfra)
				.orElse(null));
		}
		
		@Override
		public void beforeTestExecution(final ExtensionContext context)
		{
			LOG.info("vvvvvv--START TEST--vvvvvv");
		}
		
		@Override
		public void afterTestExecution(final ExtensionContext context) throws Exception
		{
			LOG.info("^^^^^^--END TEST--^^^^^^");
			final Optional<Throwable> executionExceptionOpt = context.getExecutionException();
			executionExceptionOpt.ifPresent(throwable -> LOG.error("Test-Failure", throwable));
			
			super.afterTestExecution(context);
		}
	}
	
	
	public static class BaseTestReaper implements LeakDetectionAsyncReaper
	{
		@Override
		public void blockUntilReaped()
		{
			AbstractBaseTest.REAP_CFS.stream()
				.filter(Objects::nonNull)
				.forEach(CompletableFuture::join);
		}
	}
	
	
	public static class Tracer implements TestExecutionListener
	{
		private static final Logger LOG = LoggerFactory.getLogger(Tracer.class);
		
		@Override
		public void testPlanExecutionFinished(final TestPlan testPlan)
		{
			LOG.info(
				"""
					=== TRACER ===
					BaseInfra: {}
					WebDriver: {}""",
				TRACE_START_BASE_INFRA,
				TRACE_START_WEB_DRIVER);
		}
	}
}
