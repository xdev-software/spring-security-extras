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
package software.xdev.sse.oauth2.checkauth.metrics;

import java.util.Collections;
import java.util.Map;

import io.micrometer.core.instrument.MeterRegistry;
import software.xdev.sse.metrics.DefaultMetricsHandler;
import software.xdev.sse.oauth2.checkauth.config.AuthProviderOfflineConfig;


public class DefaultOAuth2ProviderOfflineManagerMetricsHandler extends DefaultMetricsHandler
	implements OAuth2ProviderOfflineManagerMetricsHandler
{
	private final MeterRegistry meterRegistry;
	
	public DefaultOAuth2ProviderOfflineManagerMetricsHandler(
		final AuthProviderOfflineConfig config,
		final MeterRegistry meterRegistry)
	{
		super(config.isDefaultMetricsEnabled());
		this.meterRegistry = meterRegistry;
	}
	
	@Override
	public void gaugeAuthProviderOffline(final Map<?, ?> map)
	{
		this.meterRegistry.gaugeMapSize(
			PREFIX + "auth_provider_offline",
			Collections.emptyList(),
			map);
	}
}
