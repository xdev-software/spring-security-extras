package software.xdev.sse.demo.tci.webapp;

import software.xdev.sse.demo.tci.webapp.containers.WebAppContainer;
import software.xdev.tci.TCI;


public abstract class WebAppTCI<C extends WebAppContainer<C>> extends TCI<C>
{
	public static final String ACTUATOR_USERNAME = "admin";
	@SuppressWarnings("java:S2068")
	public static final String ACTUATOR_PASSWORD = ACTUATOR_USERNAME;
	@SuppressWarnings("java:S2068")
	// PW = admin
	public static final String ACTUATOR_PW_SHA256 = "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918";
	
	protected WebAppTCI(final C container, final String networkAlias)
	{
		super(container, networkAlias);
	}
	
	public String getActuatorUsername()
	{
		return ACTUATOR_USERNAME;
	}
	
	public String getActuatorPassword()
	{
		return ACTUATOR_PASSWORD;
	}
	
	public String getInternalHTTPEndpoint()
	{
		return "http://" + this.getNetworkAlias() + ":" + WebAppContainer.DEFAULT_HTTP_PORT;
	}
	
	public String getExternalHTTPEndpoint()
	{
		return "http://" + this.getContainer().getHost()
			+ ":" + this.getContainer().getMappedPort(WebAppContainer.DEFAULT_HTTP_PORT);
	}
}
