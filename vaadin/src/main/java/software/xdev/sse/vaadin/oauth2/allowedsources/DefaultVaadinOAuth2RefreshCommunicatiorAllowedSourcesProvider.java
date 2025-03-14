package software.xdev.sse.vaadin.oauth2.allowedsources;

import java.util.Set;

import software.xdev.sse.oauth2.filter.reloadcom.OAuth2RefreshReloadCommunicator;


public class DefaultVaadinOAuth2RefreshCommunicatiorAllowedSourcesProvider
	implements VaadinOAuth2RefreshCommunicatiorAllowedSourcesProvider
{
	
	@Override
	public Set<OAuth2RefreshReloadCommunicator.Source> allowedSources()
	{
		return Set.of(OAuth2RefreshReloadCommunicator.Source.values());
	}
}
