package software.xdev.sse.demo.tci.webapp.factory;

import java.util.function.Consumer;

import software.xdev.sse.demo.tci.webapp.VaadinWebAppTCI;
import software.xdev.sse.demo.tci.webapp.containers.VaadinWebAppContainer;
import software.xdev.tci.factory.prestart.PreStartableTCIFactory;


public class VaadinWebAppPreStartableTCIFactory extends PreStartableTCIFactory<VaadinWebAppContainer, VaadinWebAppTCI>
{
	public VaadinWebAppPreStartableTCIFactory(final Consumer<VaadinWebAppContainer> additionalContainerBuilder)
	{
		super(
			VaadinWebAppTCI::new,
			() -> {
				final VaadinWebAppContainer container = VaadinWebAppTCIFactoryBase.createDefaultContainer();
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
		VaadinWebAppImageNameResolver.getAppImageName();
		super.warmUpInternal();
	}
}
