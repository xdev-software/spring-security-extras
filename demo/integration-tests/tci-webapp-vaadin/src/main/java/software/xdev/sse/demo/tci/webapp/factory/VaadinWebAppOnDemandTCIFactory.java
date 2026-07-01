package software.xdev.sse.demo.tci.webapp.factory;

import java.util.function.Consumer;

import software.xdev.sse.demo.tci.webapp.VaadinWebAppTCI;
import software.xdev.sse.demo.tci.webapp.containers.VaadinWebAppContainer;
import software.xdev.tci.factory.ondemand.OnDemandTCIFactory;


public class VaadinWebAppOnDemandTCIFactory extends OnDemandTCIFactory<VaadinWebAppContainer, VaadinWebAppTCI>
{
	public VaadinWebAppOnDemandTCIFactory(
		final String variantName,
		final Consumer<VaadinWebAppContainer> additionalContainerBuilder)
	{
		super(
			VaadinWebAppTCI::new,
			() -> {
				final VaadinWebAppContainer container = VaadinWebAppTCIFactoryBase.createDefaultContainer();
				additionalContainerBuilder.accept(container);
				return container;
			},
			"webapp-" + variantName,
			"container.webapp-" + variantName);
	}
	
	@Override
	protected void warmUpInternal()
	{
		VaadinWebAppImageNameResolver.getAppImageName();
		super.warmUpInternal();
	}
}
