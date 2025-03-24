package software.xdev.sse.demo.tci.selenium;

import static java.util.Collections.emptyMap;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.ClientConfig;
import org.openqa.selenium.remote.http.HttpClient;
import org.rnorth.ducttape.timeouts.Timeouts;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.lifecycle.TestDescription;

import software.xdev.sse.TCI;
import software.xdev.sse.demo.tci.selenium.containers.SeleniumBrowserWebDriverContainer;


public class BrowserTCI extends TCI<SeleniumBrowserWebDriverContainer>
{
	private static final Logger LOG = LoggerFactory.getLogger(BrowserTCI.class);
	
	protected Capabilities capabilities;
	protected RemoteWebDriver webDriver;
	protected ClientConfig clientConfig = ClientConfig.defaultConfig();
	protected int webDriverRetryCount = 2;
	protected int webDriverRetrySec = 30;
	
	public BrowserTCI(
		final SeleniumBrowserWebDriverContainer container,
		final String networkAlias,
		final Capabilities capabilities)
	{
		super(container, networkAlias);
		this.capabilities = capabilities;
	}
	
	public BrowserTCI withClientConfig(final ClientConfig clientConfig)
	{
		this.clientConfig = Objects.requireNonNull(clientConfig);
		return this;
	}
	
	public BrowserTCI withWebDriverRetryCount(final int webDriverRetryCount)
	{
		this.webDriverRetryCount = Math.min(Math.max(webDriverRetryCount, 2), 10);
		return this;
	}
	
	public BrowserTCI withWebDriverRetrySec(final int webDriverRetrySec)
	{
		this.webDriverRetrySec = Math.min(Math.max(webDriverRetrySec, 10), 10 * 60);
		return this;
	}
	
	@Override
	public void start(final String containerName)
	{
		super.start(containerName);
		
		this.initWebDriver();
	}
	
	@SuppressWarnings("checkstyle:MagicNumber")
	protected void initWebDriver()
	{
		LOG.debug("Initializing WebDriver");
		final AtomicInteger retryCounter = new AtomicInteger(1);
		this.webDriver = Unreliables.retryUntilSuccess(
			this.webDriverRetryCount,
			() -> {
				final ClientConfig config =
					this.clientConfig.baseUri(this.getContainer().getSeleniumAddressURI());
				final int tryCount = retryCounter.getAndIncrement();
				LOG.info(
					"Creating new WebDriver [retryCount={},retrySec={},clientConfig={}] Try #{}",
					this.webDriverRetryCount,
					this.webDriverRetrySec,
					config,
					tryCount);
				
				final HttpClient.Factory factory = HttpCommandExecutor.getDefaultClientFactory();
				final HttpClient client = factory.createClient(config);
				final HttpCommandExecutor commandExecutor = new HttpCommandExecutor(
					emptyMap(),
					config,
					// Constructor without factory does not exist...
					x -> client);
				
				try
				{
					return Timeouts.getWithTimeout(
						this.webDriverRetrySec,
						TimeUnit.SECONDS,
						() -> new RemoteWebDriver(commandExecutor, this.capabilities));
				}
				catch(final RuntimeException rex)
				{
					// Cancel further communication and abort all connections
					try
					{
						LOG.warn("Encounter problem in try #{} - Terminating communication", tryCount);
						client.close();
						factory.cleanupIdleClients();
					}
					catch(final Exception ex)
					{
						LOG.warn("Failed to cleanup try #{}", tryCount, ex);
					}
					
					throw rex;
				}
			});
		
		// Default timeout is 5m? -> Single test failure causes up to 10m delays (replay must also be saved!)
		// https://w3c.github.io/webdriver/#timeouts
		this.webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
		
		// Maximize window
		this.webDriver.manage().window().maximize();
	}
	
	public Optional<String> getVncAddress()
	{
		return Optional.ofNullable(this.getContainer().getVncAddress());
	}
	
	public Optional<String> getNoVncAddress()
	{
		return Optional.ofNullable(this.getContainer().getNoVncAddress());
	}
	
	public RemoteWebDriver getWebDriver()
	{
		return this.webDriver;
	}
	
	public void afterTest(final TestDescription description, final Optional<Throwable> throwable)
	{
		if(this.getContainer() != null)
		{
			this.getContainer().afterTest(description, throwable);
		}
	}
	
	@Override
	public void stop()
	{
		if(this.webDriver != null)
		{
			final long startTime = System.currentTimeMillis();
			try
			{
				this.webDriver.quit();
			}
			catch(final Exception e)
			{
				LOG.warn("Failed to quit the driver", e);
			}
			finally
			{
				if(LOG.isDebugEnabled())
				{
					LOG.debug("Quiting driver took {}ms", System.currentTimeMillis() - startTime);
				}
			}
			this.webDriver = null;
		}
		super.stop();
	}
}
