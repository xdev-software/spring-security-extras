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
package software.xdev.sse.vaadin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.vaadin.flow.spring.security.VaadinWebSecurity;


/**
 * Override of {@link VaadinWebSecurity} that doesn't allow any VaadinSession to be created without previous
 * authentication.
 */
public abstract class TotalVaadinFlowWebSecurity extends VaadinWebSecurity
{
	@Autowired
	protected SecureVaadinRequestCache vaadinDefaultRequestCache;
	
	protected RequestMatcher csrfActiveRequestMatcher;
	
	@SuppressWarnings("java:S4502") // Vaadin brings its own CSRF
	@Override
	protected void configure(final HttpSecurity http) throws Exception
	{
		// IMPORTANT - SECURITY
		// Not using "super.configure(http)" here due to security problems, see below #configureAuthorizeHttpRequests
		
		http.exceptionHandling(this::configureExceptionHandling);
		
		// Removed CSRF as Vaadin bring its own CSRF
		// Otherwise sessions are created on rogue POST request
		// If CSRF is required for other parts of the app, whitelist them using requireCsrfProtectionMatcher
		if(this.csrfActiveRequestMatcher == null)
		{
			// NOTE that Springs default logout view will not show up when CSRF is disabled!
			http.csrf(AbstractHttpConfigurer::disable);
		}
		else
		{
			http.csrf(c -> c.ignoringRequestMatchers(request -> !this.csrfActiveRequestMatcher.matches(request)));
		}
		
		// NOTE: Creates session by default for redirect after auth
		// Example: /settings -> /login -> OIDC-Server -> /login/oauth2/code/... -> /settings
		// See also HttpSessionRequestCache#createSessionAllowed
		http.requestCache(cfg -> cfg.requestCache(this.vaadinDefaultRequestCache));
		
		http.authorizeHttpRequests(this::configureAuthorizeHttpRequests);
		
		this.getNavigationAccessControl().setEnabled(this.enableNavigationAccessControl());
	}
	
	protected void configureExceptionHandling(final ExceptionHandlingConfigurer<HttpSecurity> cfg)
	{
		// Removed support for Hilla endpoints
		cfg.accessDeniedHandler(this.createAccessDeniedHandler());
	}
	
	protected void configureAuthorizeHttpRequests(
		final AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry urlRegistry)
	{
		// By default, Vaadin configures authorizeHttpRequests to allow anonymous requests for certain
		// endpoints like /favicon.ico or /VAADIN/**
		// This is a problem because:
		// 1. A new (heavy) Vaadin session is created, which might cause Session leak and performance problems
		// 2. There is no user associated with it which the app is not designed for
		
		// The registered filters may also cause a performance impact
		
		// ALL VAADIN REQUESTS REQUIRE AUTHENTICATION
		urlRegistry.anyRequest().authenticated();
	}
	
	public void setCsrfActiveRequestMatcher(final RequestMatcher csrfActiveRequestMatcher)
	{
		this.csrfActiveRequestMatcher = csrfActiveRequestMatcher;
	}
	
	// Copied from super (as it's private) and removed hilla
	@SuppressWarnings("java:S2177")
	protected AccessDeniedHandler createAccessDeniedHandler()
	{
		return new AccessDeniedHandlerImpl();
	}
}
