package software.xdev.sse.demo.tci.webapp.factory;

import java.util.function.Supplier;

import software.xdev.sse.demo.tci.webapp.containers.WebAppContainerBuilder;
import software.xdev.tci.concurrent.Suppliers;


public final class VaadinWebAppImageNameResolver
{
	private static final Supplier<String> IMAGE_NAME_SUPPLIER =
		Suppliers.memoize(() -> WebAppContainerBuilder.getImageName("tci-webapp-vaadin"));
	
	public static String get()
	{
		return IMAGE_NAME_SUPPLIER.get();
	}
	
	private VaadinWebAppImageNameResolver()
	{
	}
}
