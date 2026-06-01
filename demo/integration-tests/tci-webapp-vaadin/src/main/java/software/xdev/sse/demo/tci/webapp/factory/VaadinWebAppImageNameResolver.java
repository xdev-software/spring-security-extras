package software.xdev.sse.demo.tci.webapp.factory;

import software.xdev.sse.demo.tci.webapp.containers.WebAppContainerBuilder;


public final class VaadinWebAppImageNameResolver
{
	public static final String PROPERTY_APP_DOCKERIMAGE = "appDockerImage";
	
	private static String appImageName;
	
	public static synchronized String getAppImageName()
	{
		if(appImageName != null)
		{
			return appImageName;
		}
		
		appImageName = System.getProperty(PROPERTY_APP_DOCKERIMAGE);
		if(appImageName == null)
		{
			appImageName = WebAppContainerBuilder.getBuiltImageName("tci-webapp-vaadin");
		}
		
		return appImageName;
	}
	
	private VaadinWebAppImageNameResolver()
	{
	}
}
