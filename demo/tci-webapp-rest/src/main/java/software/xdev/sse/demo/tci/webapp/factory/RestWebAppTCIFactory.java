package software.xdev.sse.demo.tci.webapp.factory;

import java.time.Duration;
import java.util.function.Consumer;

import software.xdev.sse.demo.tci.util.ContainerMemory;
import software.xdev.sse.demo.tci.webapp.RestWebAppTCI;
import software.xdev.sse.demo.tci.webapp.WebAppTCI;
import software.xdev.sse.demo.tci.webapp.containers.RestWebAppContainer;
import software.xdev.sse.demo.tci.webapp.containers.WebAppContainerBuilder;
import software.xdev.tci.factory.prestart.PreStartableTCIFactory;


public class RestWebAppTCIFactory extends PreStartableTCIFactory<RestWebAppContainer, RestWebAppTCI>
{
	public static final String PROPERTY_APP_DOCKERIMAGE = "appDockerImage";
	
	protected static String appImageName;
	
	public RestWebAppTCIFactory(final Consumer<RestWebAppContainer> additionalContainerBuilder)
	{
		super(
			RestWebAppTCI::new,
			() -> {
				final RestWebAppContainer container = new RestWebAppContainer(getAppImageName(), true)
					.withDefaultWaitStrategy(
						Duration.ofMinutes(1),
						WebAppTCI.ACTUATOR_USERNAME,
						WebAppTCI.ACTUATOR_PASSWORD)
					.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(ContainerMemory.M1G))
					// ACTUATOR
					.withActuator(
						WebAppTCI.ACTUATOR_USERNAME,
						WebAppTCI.ACTUATOR_PW_SHA256)
					// Configure for Tests
					.withDisableHTTPS();
				additionalContainerBuilder.accept(container);
				return container;
			},
			"webapp",
			"container.webapp",
			"WebApp");
	}
	
	@Override
	protected void warmUpInternal()
	{
		getAppImageName();
		super.warmUpInternal();
	}
	
	protected static synchronized String getAppImageName()
	{
		if(appImageName != null)
		{
			return appImageName;
		}
		
		appImageName = System.getProperty(PROPERTY_APP_DOCKERIMAGE);
		if(appImageName == null)
		{
			appImageName = WebAppContainerBuilder.getBuiltImageName("tci-webapp-rest");
		}
		
		return appImageName;
	}
}
