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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.sse.oauth2.checkauth.config.AuthProviderOfflineConfig;
import software.xdev.sse.oauth2.checkauth.metrics.OAuth2ProviderOfflineManagerMetricsHandler;


/**
 * Manages the on/offline state of OAuth2Providers
 */
public class OAuth2ProviderOfflineManager
{
	private static final Logger LOG = LoggerFactory.getLogger(OAuth2ProviderOfflineManager.class);
	
	protected AuthProviderOfflineConfig config;
	protected final Map<String, ProviderOfflineState> offlineProviderRegistrationIDs = new ConcurrentHashMap<>();
	
	public OAuth2ProviderOfflineManager(
		final AuthProviderOfflineConfig config,
		final List<OAuth2ProviderOfflineManagerMetricsHandler> metricsHandlers)
	{
		this(config, metricsHandlers, false);
	}
	
	protected OAuth2ProviderOfflineManager(
		final AuthProviderOfflineConfig config,
		final List<OAuth2ProviderOfflineManagerMetricsHandler> metricsHandlers,
		final boolean silent)
	{
		this.config = config;
		if(!silent)
		{
			LOG.info("Instantiated with {}", this.config);
		}
		
		metricsHandlers.stream()
			.filter(OAuth2ProviderOfflineManagerMetricsHandler::enabled)
			.forEach(handler -> handler.gaugeAuthProviderOffline(this.offlineProviderRegistrationIDs));
	}
	
	public boolean isEnabled()
	{
		return this.config.isEnabled();
	}
	
	public ProviderState getState(final String clientRegId)
	{
		return new ProviderState(
			clientRegId,
			this.isEnabled() ? this.offlineProviderRegistrationIDs.get(clientRegId) : null);
	}
	
	protected Instant now()
	{
		return Instant.now();
	}
	
	/**
	 * @return <code>true</code> when max offline time was reached which means auth should be considered invalid
	 */
	public boolean markAsOffline(final ProviderState providerState)
	{
		if(!this.isEnabled())
		{
			return true;
		}
		final Optional<ProviderOfflineState> optProviderOfflineState = providerState.optProviderOfflineState();
		if(optProviderOfflineState
			.map(ProviderOfflineState::invalidateAuth)
			.orElse(false))
		{
			return true;
		}
		
		final Optional<Instant> prevSince = optProviderOfflineState.map(ProviderOfflineState::since);
		
		final ProviderOfflineState offlineState = new ProviderOfflineState(
			// Use previous since if possible
			prevSince.orElseGet(this::now),
			this.now().plus(this.config.getRecheckInterval()),
			prevSince
				.map(since -> !this.now().minus(this.config.getMaxOffline()).isBefore(since))
				.orElse(false));
		
		final String regId = providerState.clientRegId();
		LOG.info("Marking auth-provider '{}' as offline: {}", regId, offlineState);
		
		providerState.setOfflineState(offlineState);
		this.offlineProviderRegistrationIDs.put(regId, offlineState);
		
		return offlineState.invalidateAuth();
	}
	
	public void markAsOnline(final ProviderState providerState)
	{
		if(!this.isEnabled())
		{
			return;
		}
		if(providerState.hasOfflineState())
		{
			final String regId = providerState.clientRegId();
			LOG.info("Marking auth-provider '{}' as online", regId);
			
			this.offlineProviderRegistrationIDs.remove(regId);
		}
	}
	
	public static class ProviderState
	{
		private final String clientRegId;
		private ProviderOfflineState offlineState;
		
		public ProviderState(final String clientRegId, final ProviderOfflineState offlineState)
		{
			this.clientRegId = clientRegId;
			this.offlineState = offlineState;
		}
		
		public String clientRegId()
		{
			return this.clientRegId;
		}
		
		public void setOfflineState(final ProviderOfflineState offlineState)
		{
			this.offlineState = offlineState;
		}
		
		public Optional<ProviderOfflineState> optProviderOfflineState()
		{
			return Optional.ofNullable(this.offlineState);
		}
		
		public boolean hasOfflineState()
		{
			return this.offlineState != null;
		}
		
		public boolean shouldCheck()
		{
			return this.shouldCheck(Instant.now());
		}
		
		public boolean shouldCheck(final Instant now)
		{
			return !this.hasOfflineState()
				|| this.optProviderOfflineState().map(s -> s.shouldCheck(now)).orElse(true);
		}
		
		@Override
		public String toString()
		{
			return "ProviderState ["
				+ "clientRegId='"
				+ this.clientRegId
				+ "', offlineState="
				+ this.offlineState
				+ "]";
		}
	}
	
	
	public record ProviderOfflineState(
		Instant since,
		Instant nextCheck,
		boolean invalidateAuth
	)
	{
		public boolean shouldCheck(final Instant now)
		{
			return this.invalidateAuth() || !now.isBefore(this.nextCheck());
		}
	}
}
