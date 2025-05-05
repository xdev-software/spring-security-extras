package software.xdev.sse.demo.tci.selenium.containers;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.openqa.selenium.Capabilities;

import software.xdev.tci.portfixation.AdditionalPortsForFixedExposingContainer;
import software.xdev.tci.safestart.SafeNamedContainerStarter;
import software.xdev.testcontainers.selenium.containers.browser.CapabilitiesBrowserWebDriverContainer;


public class SeleniumBrowserWebDriverContainer
	extends CapabilitiesBrowserWebDriverContainer<SeleniumBrowserWebDriverContainer>
	implements AdditionalPortsForFixedExposingContainer
{
	public SeleniumBrowserWebDriverContainer(final Capabilities capabilities)
	{
		super(capabilities);
	}
	
	@Override
	public Set<Integer> getAdditionalTCPPortsForFixedExposing()
	{
		final Set<Integer> ports = new HashSet<>(Set.of(SELENIUM_PORT));
		if(!this.disableVNC)
		{
			if(this.exposeVNCPort)
			{
				ports.add(VNC_PORT);
			}
			if(this.enableNoVNC)
			{
				ports.add(NO_VNC_PORT);
			}
		}
		return ports;
	}
	
	@Override
	protected void doStart()
	{
		super.doStart();
		if(this.recordingContainer != null)
		{
			this.startRecordingContainerInternal();
			this.dockerClient.pauseContainerCmd(this.recordingContainer.getContainerId()).exec();
		}
	}
	
	protected void startRecordingContainerInternal()
	{
		Optional.ofNullable(this.recordingContainer)
			.map(c -> new SafeNamedContainerStarter<>("recorder-" + this.getContainerNameCleaned(), c)
				.withAttachRandomUUID(false))
			.ifPresent(SafeNamedContainerStarter::start);
	}
	
	@Override
	public void startRecordingContainer()
	{
		if(this.recordingContainer != null)
		{
			this.dockerClient.unpauseContainerCmd(this.recordingContainer.getContainerId()).exec();
		}
	}
}
