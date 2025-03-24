package software.xdev.sse.demo.tci.webapp.factory;

import java.time.Duration;
import java.util.function.Consumer;

import software.xdev.sse.demo.tci.util.ContainerMemory;
import software.xdev.sse.demo.tci.webapp.WebAppTCI;
import software.xdev.sse.demo.tci.webapp.containers.WebAppContainer;
import software.xdev.sse.demo.tci.webapp.containers.WebAppContainerBuilder;
import software.xdev.tci.factory.prestart.PreStartableTCIFactory;


public class WebAppTCIFactory extends PreStartableTCIFactory<WebAppContainer, WebAppTCI>
{
	public static final String PROPERTY_APP_DOCKERIMAGE = "appDockerImage";
	
	protected static String appImageName;
	
	public WebAppTCIFactory(final Consumer<WebAppContainer> additionalContainerBuilder)
	{
		super(
			WebAppTCI::new,
			() -> {
				final WebAppContainer container = new WebAppContainer(getAppImageName(), true)
					.withDefaultWaitStrategy(
						Duration.ofMinutes(1),
						WebAppTCI.ACTUATOR_USERNAME,
						WebAppTCI.ACTUATOR_PASSWORD)
					.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(ContainerMemory.M1G))
					// ACTUATOR
					.withActuator(
						WebAppTCI.ACTUATOR_USERNAME,
						// PW = admin SHA256
						"8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918")
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
			appImageName = WebAppContainerBuilder.getBuiltImageName();
		}
		
		return appImageName;
	}
}
