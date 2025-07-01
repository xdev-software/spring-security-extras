package software.xdev.sse.demo.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import software.xdev.sse.demo.buisness.service.UserService;
import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.oauth2.userenrichment.OAuth2UserEnricher;
import software.xdev.sse.oauth2.userinfo.OidcUserService;


@Component
public class AuthUserService extends OidcUserService
{
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected AuthUserEnricher userEnricher;
	
	@Override
	protected boolean shouldRetrieveUserInfo(final OidcUserRequest userRequest)
	{
		// Check if required data is NOT already present
		if(Optional.ofNullable(userRequest.getIdToken())
			.map(t -> t.getEmail() == null || t.getFullName() == null)
			.orElse(true))
		{
			return super.shouldRetrieveUserInfo(userRequest);
		}
		// If data is already present don't fetch additional data
		return false;
	}
	
	@Override
	public OidcUser loadUser(final OidcUserRequest req)
	{
		final OAuth2UserEnricher.EnrichmentContainer<OidcUser, UserDetail> container =
			this.userEnricher.enrich(super.loadUser(req), true);
		
		this.userService.updateLastLoginToNowAsync(container.enrichmentData().getId());
		// Logging like "loaded user..." could also go here
		
		return container.user();
	}
}
