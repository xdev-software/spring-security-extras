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
package software.xdev.sse.oauth2.checkauth;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;

import software.xdev.sse.oauth2.checkauth.disabledcheck.OAuth2IsDisabledChecker;


/**
 * Checks the give OAuth2 auth. Also respects the following
 * <ul>
 *     <li>auth provider on/offline state - {@link OAuth2ProviderOfflineManager}</li>
 *     <li>accessToken expiration - gets a new one if possible</li>
 * </ul>
 */
public class OAuth2AuthChecker
{
	private static final Logger LOG = LoggerFactory.getLogger(OAuth2AuthChecker.class);
	
	protected final OAuth2AuthorizedClientManager clientManager;
	protected final OAuth2ProviderOfflineManager providerStateManager;
	protected final OAuth2IsDisabledChecker isDisabledChecker;
	
	protected final Set<OAuth2AuthorizedClient> invalidatedClientsCache =
		Collections.newSetFromMap(new WeakHashMap<>());
	
	// Use locks instead of synchronized
	// https://docs.pmd-code.org/pmd-doc-7.5.0/pmd_rules_java_multithreading.html#avoidsynchronizedstatement
	// https://openjdk.org/jeps/8337395
	protected final Map<OAuth2AuthorizedClient, ReentrantLock> clientLocks =
		Collections.synchronizedMap(new WeakHashMap<>());
	
	public OAuth2AuthChecker(
		final OAuth2AuthorizedClientManager clientManager,
		final OAuth2ProviderOfflineManager providerStateManager,
		final OAuth2IsDisabledChecker isDisabledChecker)
	{
		this.clientManager = clientManager;
		this.providerStateManager = providerStateManager;
		this.isDisabledChecker = isDisabledChecker;
	}
	
	// There is nothing to abstract here
	@SuppressWarnings({"java:S3776", "PMD.CognitiveComplexity", "PMD.AvoidDeeplyNestedIfStmts"})
	public AuthCheckResult check(
		final OAuth2AuthenticationToken auth,
		final BiFunction<String, String, OAuth2AuthorizedClient> getClient)
	{
		final String clientRegId = auth.getAuthorizedClientRegistrationId();
		final OAuth2ProviderOfflineManager.ProviderState providerState =
			this.providerStateManager.getState(clientRegId);
		if(!providerState.shouldCheck())
		{
			return new AuthCheckResult(AuthCheckOutcome.AUTH_PROVIDER_UNAVAILABLE_CACHED);
		}
		
		final OAuth2AuthorizedClient client = getClient.apply(clientRegId, auth.getName());
		// This should never happen
		if(client == null)
		{
			LOG.warn("Unexpected state - No client found for '{}'", auth.getPrincipal().getName());
			return new AuthCheckResult(AuthCheckOutcome.DE_AUTH);
		}
		// Current token is valid -> we are fine
		if(this.isOAuth2TokenValid(client.getAccessToken()))
		{
			return new AuthCheckResult(AuthCheckOutcome.VALID);
		}
		
		final ReentrantLock lock = this.clientLocks.computeIfAbsent(client, c -> new ReentrantLock());
		try
		{
			lock.lock();
			
			// The current client was already processed and is now invalid; No further action is required
			if(this.invalidatedClientsCache.contains(client))
			{
				LOG.debug("Ignoring check; Client was already invalidated");
				return new AuthCheckResult(AuthCheckOutcome.CLIENT_ALREADY_INVALIDATED);
			}
			
			// Try to refresh. If it fails invalidate the session
			if(this.isOAuth2TokenValid(client.getRefreshToken())
				&& !this.isUserDisabled(auth))
			{
				boolean invalidateCurrentClient = true;
				boolean providerOnline = true;
				try
				{
					LOG.debug("Refreshing tokens for '{}'", auth.getName());
					final OAuth2AuthorizedClient newClient = this.clientManager.authorize(OAuth2AuthorizeRequest
						.withAuthorizedClient(client)
						.principal(auth)
						.build());
					if(newClient != null)
					{
						return new AccessTokenRefreshAuthCheckResult(newClient);
					}
				}
				catch(final Exception e)
				{
					if(e instanceof final ClientAuthorizationException cae)
					{
						final String errorCode = cae.getError().getErrorCode();
						// Refresh token expired/revoked
						if(OAuth2ErrorCodes.INVALID_GRANT.equalsIgnoreCase(errorCode))
						{
							LOG.debug("Refresh token is invalid (likely expired or got revoked)", e);
						}
						// Communication problem with Auth provider
						// DefaultRefreshTokenTokenResponseClient#INVALID_TOKEN_RESPONSE_ERROR_CODE
						else if("invalid_token_response".equals(errorCode))
						{
							providerOnline = false;
							LOG.warn("Communication problem with auth-provider '{}'", clientRegId, e);
							if(!this.providerStateManager.markAsOffline(providerState))
							{
								// Otherwise token refresh will never be called again
								invalidateCurrentClient = false;
								// Keep auth
								return new AuthCheckResult(AuthCheckOutcome.AUTH_PROVIDER_TEMP_OFFLINE);
							}
						}
						else
						{
							LOG.warn("Failed to refresh token", e);
						}
					}
					else
					{
						LOG.warn("Failed to refresh token", e);
					}
				}
				finally
				{
					if(invalidateCurrentClient)
					{
						this.invalidatedClientsCache.add(client);
					}
					if(providerOnline)
					{
						this.providerStateManager.markAsOnline(providerState);
					}
				}
			}
		}
		finally
		{
			lock.unlock();
		}
		
		return new AuthCheckResult(AuthCheckOutcome.DE_AUTH);
	}
	
	protected boolean isOAuth2TokenValid(final AbstractOAuth2Token token)
	{
		if(token == null)
		{
			return false;
		}
		
		final Instant expiresAt = token.getExpiresAt();
		return expiresAt == null || expiresAt.isAfter(Instant.now());
	}
	
	protected boolean isUserDisabled(final OAuth2AuthenticationToken auth)
	{
		return this.isDisabledChecker.isDisabled(auth);
	}
	
	public static class AuthCheckResult
	{
		final AuthCheckOutcome outcome;
		
		public AuthCheckResult(final AuthCheckOutcome outcome)
		{
			this.outcome = outcome;
		}
		
		public AuthCheckOutcome outcome()
		{
			return this.outcome;
		}
	}
	
	
	public static class AccessTokenRefreshAuthCheckResult extends AuthCheckResult
	{
		final OAuth2AuthorizedClient newClient;
		
		public AccessTokenRefreshAuthCheckResult(final OAuth2AuthorizedClient newClient)
		{
			super(AuthCheckOutcome.ACCESS_TOKEN_REFRESH);
			this.newClient = newClient;
		}
		
		public OAuth2AuthorizedClient newClient()
		{
			return this.newClient;
		}
	}
	
	
	public enum AuthCheckOutcome
	{
		VALID("valid"),
		AUTH_PROVIDER_UNAVAILABLE_CACHED("auth_provider_unavailable_cached"),
		CLIENT_ALREADY_INVALIDATED("client_already_invalidated"),
		ACCESS_TOKEN_REFRESH("access_token_refresh"),
		AUTH_PROVIDER_TEMP_OFFLINE("auth_provider_temp_offline"),
		DE_AUTH("deauth");
		
		final String metricsOutcome;
		
		AuthCheckOutcome(final String metricsOutcome)
		{
			this.metricsOutcome = metricsOutcome;
		}
		
		public String metricsOutcome()
		{
			return this.metricsOutcome;
		}
	}
}
