package software.xdev.sse.demo.tci.webapp.containers;

@SuppressWarnings("java:S2160")
public class RestWebAppContainer extends WebAppContainer<RestWebAppContainer>
{
	public RestWebAppContainer(final String dockerImageName, final boolean connectionlessStart)
	{
		super(dockerImageName, connectionlessStart);
	}
}
