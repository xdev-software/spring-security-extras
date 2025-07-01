package software.xdev.sse.demo.tci.selenium.factory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.openqa.selenium.Capabilities;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.images.RemoteDockerImage;

import software.xdev.sse.demo.tci.selenium.BrowserTCI;
import software.xdev.sse.demo.tci.selenium.TestBrowser;
import software.xdev.sse.demo.tci.selenium.containers.SeleniumBrowserWebDriverContainer;
import software.xdev.tci.factory.TCIFactory;
import software.xdev.tci.tracing.TCITracer;
import software.xdev.testcontainers.selenium.containers.recorder.SeleniumRecordingContainer;


public class BrowsersTCIFactory implements TCIFactory<SeleniumBrowserWebDriverContainer, BrowserTCI>
{
	private final Map<String, BrowserTCIFactory> browserFactories = Collections.synchronizedMap(new HashMap<>());
	private boolean alreadyWarmedUp;
	
	public BrowsersTCIFactory()
	{
		Arrays.stream(TestBrowser.values())
			.map(TestBrowser::getCapabilityFactory)
			.map(Supplier::get)
			.forEach(cap -> this.browserFactories.put(cap.getBrowserName(), new BrowserTCIFactory(cap)));
	}
	
	@Override
	public void warmUp()
	{
		if(this.alreadyWarmedUp)
		{
			return;
		}
		
		this.warmUpInternal();
	}
	
	protected synchronized void warmUpInternal()
	{
		if(this.alreadyWarmedUp)
		{
			return;
		}
		
		this.browserFactories.values().forEach(BrowserTCIFactory::warmUp);
		
		// Pull video recorder
		CompletableFuture.runAsync(() -> {
			try
			{
				new RemoteDockerImage(SeleniumRecordingContainer.DEFAULT_IMAGE).get();
			}
			catch(final Exception e)
			{
				LoggerFactory.getLogger(this.getClass())
					.warn("Failed to pull {}", SeleniumRecordingContainer.DEFAULT_IMAGE, e);
			}
		});
		this.alreadyWarmedUp = true;
	}
	
	@SuppressWarnings("resource")
	public BrowserTCI getNew(final Capabilities capabilities, final Network network, final String... networkAliases)
	{
		return this.browserFactories.computeIfAbsent(
				capabilities.getBrowserName(),
				x -> new BrowserTCIFactory(capabilities))
			.getNew(network, networkAliases);
	}
	
	@Override
	public void close()
	{
		final List<CompletableFuture<Void>> cfFactories = this.browserFactories.values().stream()
			.map(f -> CompletableFuture.runAsync(f::close))
			.toList();
		cfFactories.forEach(CompletableFuture::join);
	}
	
	@Override
	public Set<BrowserTCI> getReturnedAndInUse()
	{
		return this.browserFactories.values()
			.stream()
			.map(BrowserTCIFactory::getReturnedAndInUse)
			.flatMap(Collection::stream)
			.collect(Collectors.toSet());
	}
	
	@Override
	public TCITracer getTracer()
	{
		return null;
	}
}
