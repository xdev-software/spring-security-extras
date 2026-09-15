package software.xdev.sse.demo.tci.webapp;

import software.xdev.sse.demo.tci.webapp.containers.RestWebAppContainer;


public class RestWebAppTCI extends WebAppTCI<RestWebAppContainer>
{
	public RestWebAppTCI(final RestWebAppContainer container, final String networkAlias)
	{
		super(container, networkAlias);
	}
}
