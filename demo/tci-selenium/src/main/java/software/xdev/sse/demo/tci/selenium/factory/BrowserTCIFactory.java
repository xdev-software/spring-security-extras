package software.xdev.sse.demo.tci.selenium.factory;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.http.ClientConfig;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;

import software.xdev.sse.demo.tci.selenium.BrowserTCI;
import software.xdev.sse.demo.tci.selenium.containers.SeleniumBrowserWebDriverContainer;
import software.xdev.tci.factory.prestart.PreStartableTCIFactory;
import software.xdev.tci.misc.ContainerMemory;
import software.xdev.testcontainers.selenium.containers.browser.BrowserWebDriverContainer;
import software.xdev.testcontainers.selenium.containers.recorder.SeleniumRecordingContainer;


class BrowserTCIFactory extends PreStartableTCIFactory<SeleniumBrowserWebDriverContainer, BrowserTCI>
{
	private static final Logger LOG = LoggerFactory.getLogger(BrowserTCIFactory.class);
	
	public static final String PROPERTY_RECORD_MODE = "recordMode";
	public static final String PROPERTY_RECORD_DIR = "recordDir";
	public static final String PROPERTY_VNC_ENABLED = "vncEnabled";
	
	public static final String DEFAULT_RECORD_DIR = "target/records";
	
	protected final String browserName;
	
	/*
	 * Constants (set by JVM-Property or default value)<br/>
	 * Only call corresponding methods and don't access the fields directly!
	 */
	protected static BrowserWebDriverContainer.RecordingMode systemRecordingMode;
	protected static Path dirForRecords;
	protected static Boolean vncEnabled;
	
	@SuppressWarnings({"resource", "checkstyle:MagicNumber"})
	public BrowserTCIFactory(final Capabilities capabilities)
	{
		super(
			(c, na) -> new BrowserTCI(c, na, capabilities)
				.withClientConfig(ClientConfig.defaultConfig()
					.readTimeout(Duration.ofSeconds(60))),
			() -> new SeleniumBrowserWebDriverContainer(capabilities)
				.withStartRecordingContainerManually(true)
				.withRecordingDirectory(getDefaultDirForRecords())
				.withRecordingMode(getDefaultRecordingMode())
				.withDisableVNC(!isVNCEnabled())
				.withEnableNoVNC(isVNCEnabled())
				.withRecordingContainerSupplier(t -> new SeleniumRecordingContainer(t)
					.withFrameRate(10)
					.withLogConsumer(getLogConsumer("container.browserrecorder." + capabilities.getBrowserName()))
					.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(ContainerMemory.M512M)))
				// Without that a mount volume dialog shows up
				// https://github.com/testcontainers/testcontainers-java/issues/1670
				.withSharedMemorySize(ContainerMemory.M2G)
				.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(ContainerMemory.M1G))
				.withEnv("SE_SCREEN_WIDTH", "1600")
				.withEnv("SE_SCREEN_HEIGHT", "900")
				// By default after 5 mins the session is killed and you can't use the container anymore. Cool or?
				// https://github.com/SeleniumHQ/docker-selenium?tab=readme-ov-file#grid-url-and-session-timeout
				.withEnv("SE_NODE_SESSION_TIMEOUT", "3600")
				// AWS's Raspberry Pi-sized CPUs are completely overloaded with the default 15s timeout so increase it
				.waitingFor(new WaitAllStrategy()
					.withStrategy(new LogMessageWaitStrategy()
						.withRegEx(".*(Started Selenium Standalone).*\n")
						.withStartupTimeout(Duration.ofMinutes(1)))
					.withStrategy(new HostPortWaitStrategy())
					.withStartupTimeout(Duration.ofMinutes(1))),
			"selenium-" + capabilities.getBrowserName().toLowerCase(),
			"container.browserwebdriver." + capabilities.getBrowserName(),
			"Browser-" + capabilities.getBrowserName());
		this.browserName = capabilities.getBrowserName();
	}
	
	@Override
	protected void postProcessNew(final BrowserTCI infra)
	{
		// Start recording container here otherwise there is a lot of blank video
		final CompletableFuture<Void> cfStartRecorder =
			CompletableFuture.runAsync(() -> infra.getContainer().startRecordingContainer());
		
		// Docker needs a few milliseconds (usually less than 100) to reconfigure its networks
		// In the meantime existing connections might fail if we go on immediately
		// So let's wait a moment here until everything is fine
		Unreliables.retryUntilSuccess(
			10,
			TimeUnit.SECONDS,
			() -> infra.getWebDriver().getCurrentUrl());
		
		cfStartRecorder.join();
	}
	
	@Override
	public String getFactoryName()
	{
		return super.getFactoryName() + "-" + this.browserName;
	}
	
	protected static synchronized BrowserWebDriverContainer.RecordingMode getDefaultRecordingMode()
	{
		if(systemRecordingMode != null)
		{
			return systemRecordingMode;
		}
		
		final String propRecordMode = System.getProperty(PROPERTY_RECORD_MODE);
		systemRecordingMode = Stream.of(BrowserWebDriverContainer.RecordingMode.values())
			.filter(rm -> rm.toString().equals(propRecordMode))
			.findFirst()
			.orElse(BrowserWebDriverContainer.RecordingMode.RECORD_FAILING);
		LOG.info("Default Recording Mode='{}'", systemRecordingMode);
		
		return systemRecordingMode;
	}
	
	protected static synchronized Path getDefaultDirForRecords()
	{
		if(dirForRecords != null)
		{
			return dirForRecords;
		}
		
		dirForRecords = Path.of(System.getProperty(PROPERTY_RECORD_DIR, DEFAULT_RECORD_DIR));
		final boolean wasCreated = dirForRecords.toFile().mkdirs();
		LOG.info("Default Directory for records='{}', created={}", dirForRecords.toAbsolutePath(), wasCreated);
		
		return dirForRecords;
	}
	
	protected static synchronized boolean isVNCEnabled()
	{
		if(vncEnabled != null)
		{
			return vncEnabled;
		}
		
		vncEnabled = Optional.ofNullable(System.getProperty(PROPERTY_VNC_ENABLED))
			.map(s -> "1".equals(s) || Boolean.parseBoolean(s))
			.orElse(false);
		LOG.info("VNC enabled={}", vncEnabled);
		
		return vncEnabled;
	}
}
