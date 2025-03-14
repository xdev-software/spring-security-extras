package software.xdev.sse.vaadin.oauth2.allowedsources;

import java.util.Set;

import software.xdev.sse.oauth2.filter.reloadcom.OAuth2RefreshReloadCommunicator;


public interface VaadinOAuth2RefreshCommunicatiorAllowedSourcesProvider
{
	Set<OAuth2RefreshReloadCommunicator.Source> allowedSources();
}
