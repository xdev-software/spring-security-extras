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

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.vaadin.flow.server.auth.NavigationAccessControl;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;

import software.xdev.sse.vaadin.csrf.VaadinCSRFDisableRequestMatcherProvider;
import software.xdev.sse.web.loginurl.LoginUrlStore;


/**
 * Wrapper for {@link VaadinSecurityConfigurer} that doesn't allow any VaadinSession to be created without previous
 * authentication.
 * <p>
 * Example usage:
 * <pre>
 * &#64;Configuration
 * &#64;EnableWebSecurity
 * public class MyWebSecurity {
 *
 *     &#64;Bean
 *     SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
 *         return http.with(new TotalVaadinFlowSecurityConfigurer(), Customizer.withDefaults()).build();
 *     }
 * }
 * </pre>
 * </p>
 */
public class TotalVaadinFlowSecurityConfigurer
	extends AbstractHttpConfigurer<TotalVaadinFlowSecurityConfigurer, HttpSecurity>
{
	protected static final Set<String> DEFAULT_DO_NOT_RESPOND_UNAUTHORIZED_METHODS = Stream.of(
			HttpMethod.GET,
			HttpMethod.OPTIONS,
			HttpMethod.HEAD,
			HttpMethod.TRACE)
		.map(HttpMethod::name)
		.collect(Collectors.toSet());
	
	protected final VaadinSecurityConfigurer vaadinSecurityConfigurer;
	
	protected Consumer<VaadinSecurityConfigurer> customizeVaadinSecurityConfigurer;
	
	public TotalVaadinFlowSecurityConfigurer()
	{
		this(VaadinSecurityConfigurer.vaadin()
			.enableCsrfConfiguration(false)
			.enableExceptionHandlingConfiguration(false)
			.enableRequestCacheConfiguration(false)
			.enableAuthorizedRequestsConfiguration(false));
	}
	
	protected TotalVaadinFlowSecurityConfigurer(final VaadinSecurityConfigurer vaadinSecurityConfigurer)
	{
		this.vaadinSecurityConfigurer = vaadinSecurityConfigurer;
	}
	
	public TotalVaadinFlowSecurityConfigurer customizeVaadin(
		final Consumer<VaadinSecurityConfigurer> customizeVaadinSecurityConfigurer)
	{
		this.customizeVaadinSecurityConfigurer = customizeVaadinSecurityConfigurer;
		return this;
	}
	
	@Override
	public void setBuilder(final HttpSecurity builder)
	{
		super.setBuilder(builder);
		this.vaadinSecurityConfigurer.setBuilder(builder);
	}
	
	@Override
	public void init(final HttpSecurity http) throws Exception
	{
		if(this.customizeVaadinSecurityConfigurer != null)
		{
			this.customizeVaadinSecurityConfigurer.accept(this.vaadinSecurityConfigurer);
		}
		this.vaadinSecurityConfigurer.init(http);
		
		this.initExceptionHandling(http);
		this.initCSRF(http);
		this.initRequestCache(http);
		this.initAuthorizeHttpRequests(http);
	}
	
	@Override
	public void configure(final HttpSecurity http) throws Exception
	{
		this.vaadinSecurityConfigurer.configure(http);
		this.configureLoginViewFromLoginUrlStore(http);
	}
	
	// region Exception Handling
	protected void initExceptionHandling(final HttpSecurity http) throws Exception
	{
		http.exceptionHandling(cfg -> {
			this.configureExceptionHandler(cfg);
			this.addDefaultAuthenticationEntryPointFor(cfg);
		});
	}
	
	protected void configureExceptionHandler(final ExceptionHandlingConfigurer<HttpSecurity> cfg)
	{
		// Removed support for Hilla endpoints
		cfg.accessDeniedHandler(this.createAccessDeniedHandler());
	}
	
	protected void addDefaultAuthenticationEntryPointFor(final ExceptionHandlingConfigurer<HttpSecurity> cfg)
	{
		// This is required for so that stuff like Vaadin's POST requests are not redirected to the
		// login site (causes JavaScript crash as HTML can't be parsed).
		cfg.defaultAuthenticationEntryPointFor(
			new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
			request -> !DEFAULT_DO_NOT_RESPOND_UNAUTHORIZED_METHODS.contains(request.getMethod()));
	}
	
	protected AccessDeniedHandler createAccessDeniedHandler()
	{
		return new AccessDeniedHandlerImpl();
	}
	
	// endregion
	
	// region CSRF
	protected void initCSRF(final HttpSecurity http) throws Exception
	{
		final List<RequestMatcher> vaadinCSRFDisableRequestMatchers = this.getApplicationContext()
			.getBeansOfType(VaadinCSRFDisableRequestMatcherProvider.class)
			.values()
			.stream()
			.map(VaadinCSRFDisableRequestMatcherProvider::getMatcher)
			.filter(Objects::nonNull)
			.toList();
		
		// Removed out-of-the-box Spring CSRF as Vaadin bring its own CSRF
		// Otherwise sessions are created on rogue POST request
		// If CSRF is required for other parts of the app, whitelist them using requireCsrfProtectionMatcher
		if(vaadinCSRFDisableRequestMatchers.isEmpty())
		{
			// NOTE that Springs default logout view will not show up when CSRF is disabled!
			http.csrf(AbstractHttpConfigurer::disable);
		}
		else
		{
			final OrRequestMatcher matcher = new OrRequestMatcher(vaadinCSRFDisableRequestMatchers);
			// Using ignoringRequestMatchers is not working as it's joined (using AND)
			// with CsrfFilter.DEFAULT_CSRF_MATCHER
			http.csrf(c -> c.requireCsrfProtectionMatcher(matcher::matches));
		}
	}
	
	// endregion
	
	// region RequestCache
	
	protected void initRequestCache(final HttpSecurity http) throws Exception
	{
		final SecureVaadinRequestCache vaadinDefaultRequestCache = Objects.requireNonNull(
			this.getApplicationContext().getBean(SecureVaadinRequestCache.class),
			"Failed to find SecureVaadinRequestCache");
		
		// NOTE: Creates session by default for redirect after auth
		// Example: /settings -> /login -> OIDC-Server -> /login/oauth2/code/... -> /settings
		// See also HttpSessionRequestCache#createSessionAllowed
		http.requestCache(cfg -> cfg.requestCache(vaadinDefaultRequestCache));
	}
	
	// endregion
	
	// region HTTP Requests
	
	protected void initAuthorizeHttpRequests(final HttpSecurity http) throws Exception
	{
		http.authorizeHttpRequests(this::configureAuthorizeHttpRequests);
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
	
	// endregion
	
	// region LoginUrl Store
	@SuppressWarnings("java:S1172") // API: Might be required downstream
	protected void configureLoginViewFromLoginUrlStore(final HttpSecurity http)
	{
		final LoginUrlStore loginUrlStore = this.getSharedObject(LoginUrlStore.class);
		if(loginUrlStore != null)
		{
			// This is usually only needed when the authentication is anonymous
			// and navigation to a view that requires non-anonymous authentication happens
			this.getSharedObject(NavigationAccessControl.class).setLoginView(loginUrlStore.getLoginUrl());
		}
	}
	// endregion
	
	protected ApplicationContext getApplicationContext()
	{
		return this.getSharedObject(ApplicationContext.class);
	}
	
	protected <T> T getSharedObject(final Class<T> clazz)
	{
		return this.getBuilder().getSharedObject(clazz);
	}
}
