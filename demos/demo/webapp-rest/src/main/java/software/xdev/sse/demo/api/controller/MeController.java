package software.xdev.sse.demo.api.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/me")
public class MeController
{
	@GetMapping
	public UserInfo me()
	{
		return Optional.ofNullable(SecurityContextHolder.getContext())
			.map(SecurityContext::getAuthentication)
			.map(Authentication::getPrincipal)
			.filter(OidcUser.class::isInstance)
			.map(OidcUser.class::cast)
			.map(UserInfo::new)
			.orElse(null);
	}
	
	public record UserInfo(
		String email,
		String name
	)
	{
		public UserInfo(final OidcUser user)
		{
			this(user.getEmail(), user.getFullName());
		}
	}
}
