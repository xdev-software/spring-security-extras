package software.xdev.sse.demo.buisness.service.session;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import software.xdev.sse.demo.buisness.service.UserService;
import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.oauth2.userenrichment.EnrichedOIDCUser;


@Service
@SessionScope
public class SessionService implements Serializable
{
	private static final Logger LOG = LoggerFactory.getLogger(SessionService.class);
	
	@Autowired
	protected transient UserService userService;
	
	private SessionUser currentUser;
	
	public SessionUser currentUser()
	{
		if(this.currentUser == null)
		{
			this.initSessionUser();
		}
		return this.currentUser;
	}
	
	public void setCurrentUser(final SessionUser currentUser)
	{
		if(this.currentUser.userID() != currentUser.userID())
		{
			throw new IllegalStateException("SessionUser can't change identity");
		}
		this.currentUser = Objects.requireNonNull(currentUser);
	}
	
	private synchronized void initSessionUser()
	{
		final Optional<OidcUser> optOIDCUser = Optional.ofNullable(SecurityContextHolder.getContext())
			.map(SecurityContext::getAuthentication)
			.map(Authentication::getPrincipal)
			.filter(OidcUser.class::isInstance)
			.map(OidcUser.class::cast);
		
		this.currentUser = optOIDCUser
			.filter(EnrichedOIDCUser.class::isInstance)
			.map(EnrichedOIDCUser.class::cast)
			.flatMap(u -> {
				// Try to utilize already loaded data during authentication so that we don't have to load it again
				if(u.getCachedAuthData() instanceof final UserDetail userDetail)
				{
					u.clearCached(); // Free up memory
					LOG.debug("Loading user from cached auth data");
					return Optional.of(userDetail);
				}
				LOG.debug("Loading user from service");
				return optOIDCUser
					.map(OidcUser::getEmail)
					.flatMap(this.userService::getUserByEmail);
			})
			.map(SessionUser::new)
			.orElseThrow();
	}
}
