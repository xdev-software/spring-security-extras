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
package software.xdev.sse.oauth2.rememberloginproviderredirect.auto;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.oauth2.cookie.CookieSecureService;
import software.xdev.sse.oauth2.rememberloginproviderredirect.CookieBasedRememberRedirectOAuth2LoginProvider;
import software.xdev.sse.oauth2.rememberloginproviderredirect.config.CookieBasedRememberOAuth2LoginProviderConfig;


@AutoConfiguration
public class CookieBasedRememberOAuth2LoginProviderAutoConfig
{
	@ConditionalOnMissingBean
	@Bean
	public CookieBasedRememberRedirectOAuth2LoginProvider cookieBasedRememberRedirectOAuth2LoginProvider(
		final CookieBasedRememberOAuth2LoginProviderConfig config,
		final CookieSecureService cookieSecureService)
	{
		return new CookieBasedRememberRedirectOAuth2LoginProvider(config, cookieSecureService);
	}
	
	@ConditionalOnMissingBean
	@Bean
	@ConfigurationProperties("sse.auth.remember-login-provider")
	public CookieBasedRememberOAuth2LoginProviderConfig cookieBasedRememberOAuth2LoginProviderConfigFallback()
	{
		return new CookieBasedRememberOAuth2LoginProviderConfig();
	}
}
