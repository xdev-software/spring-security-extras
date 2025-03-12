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
package software.xdev.sse.oauth2.rememberme.auto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import io.micrometer.core.instrument.MeterRegistry;
import software.xdev.sse.oauth2.checkauth.OAuth2AuthChecker;
import software.xdev.sse.oauth2.cookie.CookieSecureService;
import software.xdev.sse.oauth2.rememberme.OAuth2CookieRememberMeServices;
import software.xdev.sse.oauth2.rememberme.OAuth2CookieRememberMeServicesCleaner;
import software.xdev.sse.oauth2.rememberme.clientstorage.RememberMeClientStorageProcessorProvider;
import software.xdev.sse.oauth2.rememberme.config.OAuth2CookieRememberMeServicesCleanupScheduleConfig;
import software.xdev.sse.oauth2.rememberme.config.OAuth2CookieRememberMeServicesConfig;
import software.xdev.sse.oauth2.rememberme.crypt.RememberMeSymCryptManager;
import software.xdev.sse.oauth2.rememberme.metrics.AutoLoginMetrics;
import software.xdev.sse.oauth2.rememberme.metrics.DefaultAutoLoginMetrics;
import software.xdev.sse.oauth2.rememberme.secrets.AuthRememberMeSecretService;
import software.xdev.sse.oauth2.rememberme.serializer.DefaultOAuth2CookieRememberMeAuthSerializer;
import software.xdev.sse.oauth2.rememberme.serializer.OAuth2CookieRememberMeAuthSerializer;
import software.xdev.sse.web.sidecar.OtherWebSecurityPaths;


@AutoConfiguration
public class OAuth2CookieRememberMeServicesAutoConfig
{
	@ConditionalOnMissingBean
	@Bean
	public OAuth2CookieRememberMeServices oAuth2CookieRememberMeServices(
		final OAuth2CookieRememberMeServicesConfig config,
		final AutoLoginMetrics autoLoginMetrics,
		@Autowired(required = false) final RememberMeSymCryptManager cryptManager,
		final RememberMeClientStorageProcessorProvider clientStorageProcessorProvider,
		final AuthRememberMeSecretService authRememberMeSecretService,
		final OAuth2CookieRememberMeAuthSerializer payloadCookieAuthSerializer,
		final OAuth2AuthorizedClientService clientService,
		final ClientRegistrationRepository clientRegistrationRepository,
		final OAuth2AuthChecker oAuth2AuthChecker,
		final CookieSecureService cookieSecureService,
		final OtherWebSecurityPaths otherWebSecurityPaths)
	{
		return new OAuth2CookieRememberMeServices(
			config,
			autoLoginMetrics,
			cryptManager,
			clientStorageProcessorProvider,
			authRememberMeSecretService,
			payloadCookieAuthSerializer,
			clientService,
			clientRegistrationRepository,
			oAuth2AuthChecker,
			cookieSecureService)
			.setIgnoreRequestMatcher(otherWebSecurityPaths.requestMatcher(false));
	}
	
	@ConditionalOnMissingBean
	@Bean
	public OAuth2CookieRememberMeAuthSerializer oAuth2CookieRememberMeAuthSerializer()
	{
		return new DefaultOAuth2CookieRememberMeAuthSerializer();
	}
	
	@ConditionalOnMissingBean
	@Bean
	public AutoLoginMetrics autoLoginMetrics(final MeterRegistry meterRegistry)
	{
		return new DefaultAutoLoginMetrics(meterRegistry);
	}
	
	@ConditionalOnMissingBean
	@Bean
	@ConfigurationProperties("sse.auth.remember-me")
	public OAuth2CookieRememberMeServicesConfig oAuth2CookieRememberMeServicesConfig()
	{
		return new OAuth2CookieRememberMeServicesConfig();
	}
	
	@ConditionalOnMissingBean
	@Bean(name = OAuth2CookieRememberMeServicesCleaner.CLEANUP_CONFIG_BEAN_NAME)
	public OAuth2CookieRememberMeServicesCleanupScheduleConfig oAuth2CookieRememberMeServicesCleanupConfig(
		final OAuth2CookieRememberMeServicesConfig config)
	{
		return config.getCleanupSchedule();
	}
	
	@ConditionalOnBean(OAuth2CookieRememberMeServices.class)
	@ConditionalOnMissingBean
	@Bean
	public OAuth2CookieRememberMeServicesCleaner oAuth2CookieRememberMeServicesCleaner(
		final OAuth2CookieRememberMeServices oAuth2CookieRememberMeServices,
		final OAuth2CookieRememberMeServicesConfig config,
		@Qualifier(OAuth2CookieRememberMeServicesCleaner.CLEANUP_CONFIG_BEAN_NAME)
		final OAuth2CookieRememberMeServicesCleanupScheduleConfig cleanupScheduleConfig,
		final AuthRememberMeSecretService authRememberMeSecretService
	)
	{
		// DO NOT INSTANTIATE WHEN DISABLED
		if(!oAuth2CookieRememberMeServices.isEnabled())
		{
			return null;
		}
		return new OAuth2CookieRememberMeServicesCleaner(config, cleanupScheduleConfig, authRememberMeSecretService);
	}
}
