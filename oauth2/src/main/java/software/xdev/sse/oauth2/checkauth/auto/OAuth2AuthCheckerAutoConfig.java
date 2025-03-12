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
package software.xdev.sse.oauth2.checkauth.auto;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

import io.micrometer.core.instrument.MeterRegistry;
import software.xdev.sse.oauth2.checkauth.EmailBasedOAuth2AuthCheckerUserService;
import software.xdev.sse.oauth2.checkauth.OAuth2AuthChecker;
import software.xdev.sse.oauth2.checkauth.OAuth2ProviderOfflineManager;
import software.xdev.sse.oauth2.checkauth.config.AuthProviderOfflineConfig;
import software.xdev.sse.oauth2.checkauth.disabledcheck.DefaultEmailOAuth2IsDisabledChecker;
import software.xdev.sse.oauth2.checkauth.disabledcheck.OAuth2IsDisabledChecker;
import software.xdev.sse.oauth2.checkauth.metrics.DefaultOAuth2ProviderOfflineManagerMetricsHandler;
import software.xdev.sse.oauth2.checkauth.metrics.OAuth2ProviderOfflineManagerMetricsHandler;


@AutoConfiguration
public class OAuth2AuthCheckerAutoConfig
{
	@ConditionalOnMissingBean
	@Bean
	public OAuth2AuthChecker oAuth2AuthChecker(
		final OAuth2AuthorizedClientManager clientManager,
		final OAuth2ProviderOfflineManager providerStateManager,
		final OAuth2IsDisabledChecker oAuth2AuthIsDisabledChecker)
	{
		return new OAuth2AuthChecker(clientManager, providerStateManager, oAuth2AuthIsDisabledChecker);
	}
	
	@ConditionalOnMissingBean
	@Bean
	public OAuth2ProviderOfflineManager oAuth2ProviderOfflineManager(
		final AuthProviderOfflineConfig config,
		final List<OAuth2ProviderOfflineManagerMetricsHandler> metricsHandlers)
	{
		return new OAuth2ProviderOfflineManager(config, metricsHandlers);
	}
	
	@ConditionalOnMissingBean
	@Bean
	public OAuth2ProviderOfflineManagerMetricsHandler defaultoAuth2ProviderOfflineManagerMetricsHandler(
		final AuthProviderOfflineConfig config,
		final MeterRegistry meterRegistry)
	{
		return new DefaultOAuth2ProviderOfflineManagerMetricsHandler(config, meterRegistry);
	}
	
	@ConditionalOnMissingBean
	@Bean
	public OAuth2IsDisabledChecker oAuth2AuthIsDisabledChecker(final EmailBasedOAuth2AuthCheckerUserService emailBasedOAuth2AuthCheckerUserService)
	{
		return new DefaultEmailOAuth2IsDisabledChecker(emailBasedOAuth2AuthCheckerUserService);
	}
	
	@ConditionalOnMissingBean
	@ConfigurationProperties("sse.auth.offline")
	@Bean
	public AuthProviderOfflineConfig authProviderOfflineConfigFallback()
	{
		return new AuthProviderOfflineConfig();
	}
}
