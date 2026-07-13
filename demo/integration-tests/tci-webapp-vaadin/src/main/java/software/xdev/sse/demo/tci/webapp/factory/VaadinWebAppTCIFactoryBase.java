package software.xdev.sse.demo.tci.webapp.factory;

import java.time.Duration;

import software.xdev.sse.demo.tci.webapp.WebAppTCI;
import software.xdev.sse.demo.tci.webapp.containers.VaadinWebAppContainer;
import software.xdev.tci.envperf.EnvironmentPerformance;
import software.xdev.tci.misc.ContainerMemory;


public final class VaadinWebAppTCIFactoryBase
{
	@SuppressWarnings({"resource", "checkstyle:MagicNumber"})
	public static VaadinWebAppContainer createDefaultContainer()
	{
		return new VaadinWebAppContainer(VaadinWebAppImageNameResolver.getAppImageName(), true)
			.withDefaultWaitStrategy(
				Duration.ofSeconds(40L + 20L * EnvironmentPerformance.cpuSlownessFactor()),
				WebAppTCI.ACTUATOR_USERNAME,
				WebAppTCI.ACTUATOR_PASSWORD)
			.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(ContainerMemory.M1G))
			.withDefaultPayloadEncryption()
			// ACTUATOR
			.withActuator(
				WebAppTCI.ACTUATOR_USERNAME,
				WebAppTCI.ACTUATOR_PW_SHA256)
			// Configure for Tests
			.withDisableHTTPS();
	}
	
	private VaadinWebAppTCIFactoryBase()
	{
	}
}
