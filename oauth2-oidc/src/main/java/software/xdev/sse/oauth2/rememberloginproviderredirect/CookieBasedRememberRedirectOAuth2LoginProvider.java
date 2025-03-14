/*
 * Copyright © 2025 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.sse.oauth2.rememberloginproviderredirect;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import software.xdev.sse.oauth2.rememberloginproviderredirect.config.CookieBasedRememberOAuth2LoginProviderConfig;
import software.xdev.sse.oauth2.util.FastCookieFinder;
import software.xdev.sse.web.cookie.CookieSecureService;


/**
 * Tries to auto auth the user to the last OAuth2 provider that was used by them.
 * <p/>
 * Addresses the following problem:
 * <p>
 * When multiple OAuth2 providers exists no auto redirect to the last OAuth2 provider is executed (unlike when using a
 * single provider).
 * </p>
 *
 * @see OAuth2LoginConfigurer#init(org.springframework.security.config.annotation.web.HttpSecurityBuilder)
 */
public class CookieBasedRememberRedirectOAuth2LoginProvider
{
	private static final Logger LOG = LoggerFactory.getLogger(CookieBasedRememberRedirectOAuth2LoginProvider.class);
	
	protected static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI =
		OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/";
	
	protected final CookieSecureService cookieSecureService;
	
	protected final CookieBasedRememberOAuth2LoginProviderConfig config;
	protected String authorizationRequestBaseUri;
	
	public CookieBasedRememberRedirectOAuth2LoginProvider(
		final CookieBasedRememberOAuth2LoginProviderConfig config,
		final CookieSecureService cookieSecureService)
	{
		this.config = config;
		this.cookieSecureService = cookieSecureService;
		
		LOG.info("Instantiated with {}", this.config);
		
		this.setAuthorizationRequestBaseUri(config.getAuthorizationRequestBaseUri());
	}
	
	protected boolean isEnabled()
	{
		return this.config.isEnabled();
	}
	
	public boolean isSecure()
	{
		return this.cookieSecureService.isSecure();
	}
	
	public String cookiePrevOauthProviderName()
	{
		return this.config.getCookieName();
	}
	
	public int cookiePrevOauthProviderMaxAge()
	{
		return (int)this.config.getExpiration().toSeconds();
	}
	
	protected void setAuthorizationRequestBaseUri(final String uri)
	{
		this.authorizationRequestBaseUri = uri != null && !uri.endsWith("/")
			? (uri + "/")
			: uri;
	}
	
	protected void setAuthorizationRequestBaseUriToDefaultIfRequired()
	{
		if(this.authorizationRequestBaseUri == null)
		{
			this.authorizationRequestBaseUri = DEFAULT_AUTHORIZATION_REQUEST_BASE_URI;
		}
	}
	
	public String authorizationRequestBaseUri()
	{
		return this.authorizationRequestBaseUri;
	}
	
	private Optional<Cookie> findPrevOAuthProviderCookie(final HttpServletRequest request)
	{
		return FastCookieFinder.findCookie(request, this.cookiePrevOauthProviderName());
	}
	
	// region Login
	public OAuth2LoginConfigurer<HttpSecurity> configureOAuth2Login(final OAuth2LoginConfigurer<HttpSecurity> c)
	{
		if(this.isEnabled())
		{
			this.determineAuthorizationRequestBaseUriIfRequired(c);
			this.setAuthorizationRequestBaseUriToDefaultIfRequired();
			
			CookieBasedLoginUrlAuthenticationEntryPoint.createAndInstall(c, this);
			new CookieBasedDefaultRedirectStrategy(this).install(c);
		}
		return c;
	}
	
	@SuppressWarnings({"java:S3011", "java:S112", "rawtypes"})
	protected void determineAuthorizationRequestBaseUriIfRequired(final OAuth2LoginConfigurer<HttpSecurity> c)
	{
		if(this.authorizationRequestBaseUri != null)
		{
			// Already set -> Nothing to do
			return;
		}
		try
		{
			final Field fAuthorizationEndpointConfig =
				OAuth2LoginConfigurer.class.getDeclaredField("authorizationEndpointConfig");
			fAuthorizationEndpointConfig.setAccessible(true);
			final OAuth2LoginConfigurer.AuthorizationEndpointConfig authorizationEndpointConfig =
				((OAuth2LoginConfigurer.AuthorizationEndpointConfig)fAuthorizationEndpointConfig.get(c));
			
			final Field fAuthorizationRequestBaseUri =
				OAuth2LoginConfigurer.AuthorizationEndpointConfig.class.getDeclaredField(
					"authorizationRequestBaseUri");
			fAuthorizationRequestBaseUri.setAccessible(true);
			
			this.setAuthorizationRequestBaseUri(
				(String)fAuthorizationRequestBaseUri.get(authorizationEndpointConfig));
		}
		catch(final Exception e)
		{
			LOG.warn("Failed to determine authorizationRequestBaseUri", e);
		}
	}
	
	protected static class CookieBasedLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint
	{
		protected final CookieBasedRememberRedirectOAuth2LoginProvider parent;
		protected final Set<String> loginUrls;
		
		public CookieBasedLoginUrlAuthenticationEntryPoint(
			final String loginFormUrl,
			final CookieBasedRememberRedirectOAuth2LoginProvider parent,
			final Set<String> loginUrls)
		{
			super(loginFormUrl);
			this.parent = parent;
			this.loginUrls = loginUrls;
		}
		
		@Override
		protected String determineUrlToUseForThisRequest(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final AuthenticationException authException)
		{
			final Cookie cookie = this.parent.findPrevOAuthProviderCookie(request).orElse(null);
			if(cookie != null)
			{
				final String value = cookie.getValue();
				if(value != null)
				{
					final String decodedValue = this.parent.authorizationRequestBaseUri()
						+ new String(Base64.getDecoder().decode(value.getBytes()));
					if(this.loginUrls.contains(decodedValue))
					{
						return decodedValue;
					}
				}
				
				// Cookie invalid -> delete
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
			
			return super.determineUrlToUseForThisRequest(request, response, authException);
		}
		
		@SuppressWarnings({"java:S3011", "java:S112", "unchecked"})
		protected static CookieBasedLoginUrlAuthenticationEntryPoint createAndInstall(
			final OAuth2LoginConfigurer<HttpSecurity> c,
			final CookieBasedRememberRedirectOAuth2LoginProvider parent)
		{
			try
			{
				final Method mGetLoginLinks = OAuth2LoginConfigurer.class.getDeclaredMethod("getLoginLinks");
				mGetLoginLinks.setAccessible(true);
				final Set<String> loginUrls = ((Map<String, String>)mGetLoginLinks.invoke(c)).keySet();
				
				final Field fAuthenticationEntryPoint =
					AbstractAuthenticationFilterConfigurer.class.getDeclaredField("authenticationEntryPoint");
				fAuthenticationEntryPoint.setAccessible(true);
				final LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint =
					(LoginUrlAuthenticationEntryPoint)fAuthenticationEntryPoint.get(c);
				final CookieBasedLoginUrlAuthenticationEntryPoint entryPoint =
					new CookieBasedLoginUrlAuthenticationEntryPoint(
						loginUrlAuthenticationEntryPoint.getLoginFormUrl(),
						parent,
						loginUrls);
				fAuthenticationEntryPoint.set(c, entryPoint);
				return entryPoint;
			}
			catch(final NoSuchFieldException | NoSuchMethodException | IllegalAccessException
						| InvocationTargetException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	
	protected static class CookieBasedDefaultRedirectStrategy extends DefaultRedirectStrategy
	{
		private final CookieBasedRememberRedirectOAuth2LoginProvider parent;
		
		public CookieBasedDefaultRedirectStrategy(final CookieBasedRememberRedirectOAuth2LoginProvider parent)
		{
			this.parent = parent;
		}
		
		@Override
		public void sendRedirect(
			final HttpServletRequest request, final HttpServletResponse response, final String url)
			throws IOException
		{
			final Cookie cookie = new Cookie(
				this.parent.cookiePrevOauthProviderName(),
				Base64.getEncoder().encodeToString(
					request.getServletPath().replace(this.parent.authorizationRequestBaseUri(), "")
						.getBytes()));
			cookie.setHttpOnly(true);
			cookie.setSecure(this.parent.isSecure());
			cookie.setMaxAge(this.parent.cookiePrevOauthProviderMaxAge());
			cookie.setPath("/");
			response.addCookie(cookie);
			
			super.sendRedirect(request, response, url);
		}
		
		protected CookieBasedDefaultRedirectStrategy install(final OAuth2LoginConfigurer<HttpSecurity> c)
		{
			c.addObjectPostProcessor(new ObjectPostProcessor<OAuth2AuthorizationRequestRedirectFilter>()
			{
				@Override
				public <O extends OAuth2AuthorizationRequestRedirectFilter> O postProcess(final O o)
				{
					o.setAuthorizationRedirectStrategy(CookieBasedDefaultRedirectStrategy.this);
					return o;
				}
			});
			return this;
		}
	}
	
	// endregion
	// region Logout
	public LogoutConfigurer<HttpSecurity> configureOAuth2Logout(final LogoutConfigurer<HttpSecurity> c)
	{
		if(this.isEnabled())
		{
			c.addLogoutHandler(new CookieLogoutHandler(this));
		}
		return c;
	}
	
	protected static class CookieLogoutHandler implements LogoutHandler
	{
		protected final CookieBasedRememberRedirectOAuth2LoginProvider parent;
		
		public CookieLogoutHandler(final CookieBasedRememberRedirectOAuth2LoginProvider parent)
		{
			this.parent = parent;
		}
		
		@Override
		public void logout(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Authentication authentication)
		{
			this.parent.findPrevOAuthProviderCookie(request)
				.ifPresent(c ->
				{
					c.setMaxAge(0); // Expire
					response.addCookie(c);
				});
		}
	}
	
	// endregion
}
