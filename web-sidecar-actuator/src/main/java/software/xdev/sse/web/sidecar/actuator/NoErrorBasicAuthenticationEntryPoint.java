package software.xdev.sse.web.sidecar.actuator;

import java.lang.reflect.Field;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;


public class NoErrorBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint
{
	protected NoErrorBasicAuthenticationEntryPoint()
	{
	}
	
	public NoErrorBasicAuthenticationEntryPoint(final BasicAuthenticationEntryPoint copyFrom)
	{
		this.setRealmName(copyFrom.getRealmName());
	}
	
	@Override
	public void commence(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final AuthenticationException authException)
	{
		response.setHeader("WWW-Authenticate", "Basic realm=\"" + this.getRealmName() + "\"");
		// It is not an error when someone sends invalid credentials!!!
		// DO NOT call sendError HERE!
		// This results in the request being redirect to /error, when error pages are enabled (default)
		// This then results in massive routing problems because /error might not be expected
		// resulting in mixed up garbage responses (e.g. BASIC AUTH + REDIRECT + OAUTH2)
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
	}
	
	@SuppressWarnings("java:S3011") // private? -> Crowbar time
	public static <B extends HttpSecurityBuilder<B>> HttpBasicConfigurer<B> install(
		final HttpBasicConfigurer<B> httpBasicConfigurer)
	{
		try
		{
			final Field fBasicAuthEntryPoint = HttpBasicConfigurer.class.getDeclaredField("basicAuthEntryPoint");
			fBasicAuthEntryPoint.setAccessible(true);
			
			final NoErrorBasicAuthenticationEntryPoint entryPoint = new NoErrorBasicAuthenticationEntryPoint(
				(BasicAuthenticationEntryPoint)fBasicAuthEntryPoint.get(httpBasicConfigurer));
			
			fBasicAuthEntryPoint.set(httpBasicConfigurer, entryPoint);
			
			final Field fAuthEntryPoint = HttpBasicConfigurer.class.getDeclaredField("authenticationEntryPoint");
			fAuthEntryPoint.setAccessible(true);
			final AuthenticationEntryPoint authenticationEntryPoint =
				(AuthenticationEntryPoint)fAuthEntryPoint.get(httpBasicConfigurer);
			
			if(authenticationEntryPoint instanceof final DelegatingAuthenticationEntryPoint delegatingAuthEntryPoint)
			{
				delegatingAuthEntryPoint.setDefaultEntryPoint(entryPoint);
			}
		}
		catch(final NoSuchFieldException | IllegalAccessException e)
		{
			throw new IllegalStateException(
				"Failed to install NoErrorBasicAuthenticationEntryPoint into HttpBasicConfigurer",
				e);
		}
		return httpBasicConfigurer;
	}
}
