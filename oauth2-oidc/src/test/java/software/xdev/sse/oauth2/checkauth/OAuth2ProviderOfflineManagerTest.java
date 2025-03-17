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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import software.xdev.sse.oauth2.checkauth.config.AuthProviderOfflineConfig;


class OAuth2ProviderOfflineManagerTest
{
	@Test
	void checkTimeFlowBehavior()
	{
		final AuthProviderOfflineConfig config = new AuthProviderOfflineConfig();
		
		config.setRecheckInterval(Duration.ofMinutes(2));
		config.setMaxOffline(Duration.ofMinutes(10));
		
		final AtomicReference<Instant> nowRef = new AtomicReference<>(from(1));
		
		final OAuth2ProviderOfflineManager manager = new MockOAuth2ProviderOfflineManager(config, nowRef);
		final OAuth2ProviderOfflineManager.ProviderState providerState = manager.getState("test");
		
		// Initial check
		assertTrue(providerState.shouldCheck(nowRef.get()));
		
		// Assume that provider is offline
		nowRef.set(from(2));
		assertFalse(manager.markAsOffline(providerState));
		// Check at same minute must fail
		assertFalse(providerState.shouldCheck(nowRef.get()));
		
		// Check a minute later must also fail
		nowRef.set(from(3));
		assertFalse(providerState.shouldCheck(nowRef.get()));
		
		// Now we should check again, due to recheck interval
		nowRef.set(from(4));
		assertTrue(providerState.shouldCheck(nowRef.get()));
		
		// A lot of time passes but still offline -> invalidate auth
		nowRef.set(from(12));
		assertTrue(manager.markAsOffline(providerState));
		assertTrue(providerState.shouldCheck(nowRef.get()));
		
		// A bit later the provider comes online again
		nowRef.set(from(15));
		assertTrue(providerState.shouldCheck(nowRef.get()));
		manager.markAsOnline(providerState);
		
		assertTrue(providerState.shouldCheck(nowRef.get()));
	}
	
	private static Instant from(final int minute)
	{
		return Instant.from(OffsetDateTime.of(2000, 1, 1, 1, minute, 0, 0, ZoneOffset.UTC));
	}
	
	public static class MockOAuth2ProviderOfflineManager extends OAuth2ProviderOfflineManager
	{
		private final AtomicReference<Instant> nowRef;
		
		public MockOAuth2ProviderOfflineManager(
			final AuthProviderOfflineConfig config,
			final AtomicReference<Instant> nowRef)
		{
			super(config, List.of());
			this.nowRef = nowRef;
		}
		
		@Override
		protected Instant now()
		{
			return this.nowRef.get();
		}
	}
}
