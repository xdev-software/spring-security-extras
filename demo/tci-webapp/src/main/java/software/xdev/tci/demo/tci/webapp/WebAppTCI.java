package software.xdev.sse.demo.tci.webapp;

import software.xdev.sse.TCI;
import software.xdev.sse.demo.tci.webapp.containers.WebAppContainer;


public class WebAppTCI extends TCI<WebAppContainer>
{
	public static final String ACTUATOR_USERNAME = "admin";
	@SuppressWarnings("java:S2068")
	public static final String ACTUATOR_PASSWORD = ACTUATOR_USERNAME;
	
	public WebAppTCI(final WebAppContainer container, final String networkAlias)
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
