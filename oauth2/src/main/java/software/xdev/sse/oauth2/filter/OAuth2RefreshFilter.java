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
package software.xdev.sse.oauth2.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Supplier;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import software.xdev.sse.oauth2.checkauth.OAuth2AuthChecker;
import software.xdev.sse.oauth2.filter.handler.OAuth2RefreshHandler;
import software.xdev.sse.oauth2.filter.metrics.OAuth2RefreshFilterAuthCheckMetrics;
import software.xdev.sse.oauth2.filter.reloadcom.OAuth2RefreshReloadCommunicator;


/**
 * This filter ensures that
 * <ul>
 *     <li>the current OAuth2 auth is valid</li>
 *     <li>if the current auth is invalid: That this is communicated</li>
 * </ul>
 */
public class OAuth2RefreshFilter extends GenericFilterBean
{
	private static final Logger LOG = LoggerFactory.getLogger(OAuth2RefreshFilter.class);
	
	protected final OAuth2RefreshFilterAuthCheckMetrics metrics;
	protected final OAuth2AuthorizedClientService clientService;
	protected final OAuth2AuthChecker oAuth2AuthChecker;
	protected final Supplier<Collection<OAuth2RefreshHandler>> oAuth2RefreshHandlersSupplier;
	protected final Supplier<Collection<OAuth2RefreshReloadCommunicator>> reloadCommunicatorsSupplier;
	
	protected Collection<OAuth2RefreshHandler> oAuth2RefreshHandlers;
	protected Collection<OAuth2RefreshReloadCommunicator> reloadCommunicators;
	
	protected RequestMatcher ignoreRequestMatcher = r -> false;
	
	public OAuth2RefreshFilter(
		final OAuth2RefreshFilterAuthCheckMetrics metrics,
		final OAuth2AuthorizedClientService clientService,
		final OAuth2AuthChecker oAuth2AuthChecker,
		final Supplier<Collection<OAuth2RefreshHandler>> oAuth2RefreshHandlersSupplier,
		final Supplier<Collection<OAuth2RefreshReloadCommunicator>> reloadCommunicatorsSupplier)
	{
		this.metrics = metrics;
		this.clientService = clientService;
		this.oAuth2AuthChecker = oAuth2AuthChecker;
		this.oAuth2RefreshHandlersSupplier = oAuth2RefreshHandlersSupplier;
		this.reloadCommunicatorsSupplier = reloadCommunicatorsSupplier;
		
		LOG.debug("Instantiated");
	}
	
	public OAuth2RefreshFilter setIgnoreRequestMatcher(final RequestMatcher ignoreRequestMatcher)
	{
		this.ignoreRequestMatcher = ignoreRequestMatcher;
		return this;
	}
	
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
		throws IOException, ServletException
	{
		this.checkAuth(request, response);
		chain.doFilter(request, response);
	}
	
	@SuppressWarnings("java:S3626") // Incorrect
	private void checkAuth(final ServletRequest request, final ServletResponse response)
	{
		if(request instanceof final HttpServletRequest httpRequest
			&& this.ignoreRequestMatcher.matches(httpRequest))
		{
			this.metrics.ignored();
			return;
		}
		
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(!(authentication instanceof final OAuth2AuthenticationToken auth))
		{
			this.communicateReload(OAuth2RefreshReloadCommunicator.Source.NO_AUTH, request, response);
			this.metrics.noAuth();
			return;
		}
		
		// Client may be null on initial login -> Do not log out
		final OAuth2AuthChecker.AuthCheckResult authCheckResult =
			this.oAuth2AuthChecker.check(auth, this.clientService::loadAuthorizedClient);
		this.metrics.authCheckMetricsIncrement(authCheckResult.outcome());
		if(authCheckResult instanceof final OAuth2AuthChecker.AccessTokenRefreshAuthCheckResult accessTokenRefreshRes)
		{
			final OAuth2AuthorizedClient newClient = accessTokenRefreshRes.newClient();
			
			this.clientService.saveAuthorizedClient(newClient, auth);
			this.oAuth2RefreshHandlers().forEach(h ->
				h.saveAuthToCookie(request, response, auth, newClient, true));
			
			LOG.debug("Refreshed and saved tokens for '{}'", auth.getName());
		}
		else if(authCheckResult.outcome() == OAuth2AuthChecker.AuthCheckOutcome.DE_AUTH)
		{
			// Failed to refresh -> Invalidate
			if(request instanceof final HttpServletRequest httpRequest
				&& response instanceof final HttpServletResponse httpResponse)
			{
				this.oAuth2RefreshHandlers().forEach(h ->
					h.logout(httpRequest, httpResponse, auth));
			}
			SecurityContextHolder.getContext().setAuthentication(null);
			
			LOG.debug("De-Authenticated '{}'", auth.getName());
			
			this.communicateReload(OAuth2RefreshReloadCommunicator.Source.DE_AUTH, request, response);
		}
		else if(authCheckResult.outcome() == OAuth2AuthChecker.AuthCheckOutcome.VALID)
		{
			this.oAuth2RefreshHandlers().forEach(h ->
				h.tryWritePendingCookieSave(request, response, auth));
		}
	}
	
	protected Collection<OAuth2RefreshHandler> oAuth2RefreshHandlers()
	{
		if(this.oAuth2RefreshHandlers == null)
		{
			this.initOAuth2RefreshHandlers();
		}
		return this.oAuth2RefreshHandlers;
	}
	
	protected void communicateReload(
		final OAuth2RefreshReloadCommunicator.Source source,
		final ServletRequest request,
		final ServletResponse response)
	{
		if(this.reloadCommunicators == null)
		{
			this.initReloadCommunicators();
		}
		this.reloadCommunicators.forEach(rc -> rc.communicate(source, request, response));
	}
	
	protected synchronized void initOAuth2RefreshHandlers()
	{
		if(this.oAuth2RefreshHandlers == null)
		{
			this.oAuth2RefreshHandlers = this.oAuth2RefreshHandlersSupplier.get();
			LOG.debug("Got {}x OAuth2RefreshHandlers", this.oAuth2RefreshHandlers.size());
		}
	}
	
	protected synchronized void initReloadCommunicators()
	{
		if(this.reloadCommunicators == null)
		{
			this.reloadCommunicators = this.reloadCommunicatorsSupplier.get();
			LOG.debug("Got {}x ReloadCommunicators", this.reloadCommunicators.size());
		}
	}
}
