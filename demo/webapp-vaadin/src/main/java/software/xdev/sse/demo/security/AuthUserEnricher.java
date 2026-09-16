package software.xdev.sse.demo.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import software.xdev.sse.demo.buisness.service.UserService;
import software.xdev.sse.demo.buisness.service.auth.usercreation.UserCreationService;
import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.oauth2.rememberme.userenrichment.OAuth2RememberMeUserEnricher;
import software.xdev.sse.oauth2.userenrichment.EnrichedOIDCUser;


@Component
public class AuthUserEnricher implements OAuth2RememberMeUserEnricher<OidcUser, UserDetail>
{
	@Autowired
	protected UserService userService;
	@Autowired
	protected UserCreationService userCreationService;
	
	@Override
	public OAuth2User enrichForRememberMe(final OAuth2User user)
	{
		return user instanceof final OidcUser oidcUser
			? this.enrich(oidcUser, false).user()
			: user;
	}
	
	/**
	 * Enriches and checks the OIDC user with application specific data.
	 * <p>
	 * Stores the fetched application specific data so that it doesn't need to be fetched again.
	 * </p>
	 */
	@Override
	public EnrichmentContainer<OidcUser, UserDetail> enrich(
		final OidcUser oidcUser,
		final boolean createUserIfNotExisting)
	{
		this.validateOIDCUser(oidcUser);
		
		final String email = this.validateAndExtractEMail(oidcUser);
		
		// Load or create the user in our DB
		final UserDetail userDetail = this.userService.getUserByEmail(email)
			.orElseGet(() -> {
				if(!createUserIfNotExisting)
				{
					throw new OAuth2AuthenticationException(new OAuth2Error(
						OAuth2ErrorCodes.ACCESS_DENIED,
						"User can't be created",
						null));
				}
				return this.userCreationService.createBasicUserOnDataBase(email, oidcUser.getFullName());
			});
		
		// Validate loaded user
		if(userDetail.isDisabled())
		{
			throw new OAuth2AuthenticationException(new OAuth2Error(
				OAuth2ErrorCodes.ACCESS_DENIED,
				"User is disabled; Please contact administrator",
				null));
		}
		
		// Load roles
		final Set<String> roles = new HashSet<>();
		
		// Code like "if user.isAdmin() add Role ADMIN" could go here
		
		return new DefaultEnrichmentContainer<>(
			new EnrichedOIDCUser<>(
				oidcUser,
				roles,
				userDetail),
			userDetail);
	}
	
	private void validateOIDCUser(final OidcUser oidcUser)
	{
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
	}
	
	private String validateAndExtractEMail(final OidcUser oidcUser)
	{
		// Check if email-domain is allowed could go here
		return oidcUser.getEmail();
	}
}
