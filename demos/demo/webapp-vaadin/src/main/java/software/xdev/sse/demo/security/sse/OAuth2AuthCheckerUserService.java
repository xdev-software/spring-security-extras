package software.xdev.sse.demo.security.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import software.xdev.sse.demo.buisness.service.UserService;
import software.xdev.sse.oauth2.checkauth.EmailBasedOAuth2AuthCheckerUserService;


@Component
public class OAuth2AuthCheckerUserService implements EmailBasedOAuth2AuthCheckerUserService
{
	@Autowired
	protected UserService userService;
	
	@Override
	public boolean isDisabled(final String email)
	{
		return this.userService.isDisabled(email);
	}
}
