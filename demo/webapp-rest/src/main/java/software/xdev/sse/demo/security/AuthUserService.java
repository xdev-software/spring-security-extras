package software.xdev.sse.demo.security;

import java.util.Optional;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import software.xdev.sse.oauth2.userinfo.OidcUserService;


@Component
public class AuthUserService extends OidcUserService
{
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
		final OidcUser oidcUser = super.loadUser(req);
		
		if(oidcUser.getFullName() == null || oidcUser.getFullName().isEmpty())
		{
			throw new OAuth2AuthenticationException(new OAuth2Error(
				OAuth2ErrorCodes.ACCESS_DENIED,
				"Invalid user name",
				null));
		}
		if(oidcUser.getEmail() == null || oidcUser.getEmail().isEmpty())
		{
			throw new OAuth2AuthenticationException(new OAuth2Error(
				OAuth2ErrorCodes.ACCESS_DENIED,
				"Invalid user email",
				null));
		}
		
		return oidcUser;
	}
}
