package software.xdev.sse.demo.tci.webapp.factory;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

import software.xdev.sse.demo.tci.webapp.RestWebAppTCI;
import software.xdev.sse.demo.tci.webapp.WebAppTCI;
import software.xdev.sse.demo.tci.webapp.containers.RestWebAppContainer;
import software.xdev.sse.demo.tci.webapp.containers.WebAppContainerBuilder;
import software.xdev.tci.concurrent.Suppliers;
import software.xdev.tci.concurrent.TCIExecutorServiceHolder;
import software.xdev.tci.factory.prestart.PreStartableTCIFactory;
import software.xdev.tci.misc.ContainerMemory;


public class RestWebAppTCIFactory extends PreStartableTCIFactory<RestWebAppContainer, RestWebAppTCI>
{
	protected static final Supplier<String> IMAGE_NAME_SUPPLIER =
		Suppliers.memoize(() -> WebAppContainerBuilder.getImageName("tci-webapp-rest"));
	
	public RestWebAppTCIFactory(final Consumer<RestWebAppContainer> additionalContainerBuilder)
	{
		super(
			RestWebAppTCI::new,
			() -> {
				final RestWebAppContainer container = new RestWebAppContainer(IMAGE_NAME_SUPPLIER.get(), true)
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
		CompletableFuture.runAsync(IMAGE_NAME_SUPPLIER::get, TCIExecutorServiceHolder.instance());
		super.warmUpInternal();
	}
}
