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
package software.xdev.sse.vaadin.oauth2.auto;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.oauth2.filter.reloadcom.OAuth2RefreshReloadCommunicator;
import software.xdev.sse.oauth2.sidecar.OAuth2LoginLogoutPathsProvider;
import software.xdev.sse.vaadin.oauth2.VaadinOAuth2RefreshReloadCommunicator;
import software.xdev.sse.vaadin.oauth2.allowedsources.DefaultVaadinOAuth2RefreshCommunicatiorAllowedSourcesProvider;
import software.xdev.sse.vaadin.oauth2.allowedsources.VaadinOAuth2RefreshCommunicatiorAllowedSourcesProvider;
import software.xdev.sse.vaadin.oauth2.csrf.OAuth2LoginLogoutCSRFDisableRequestMatcherProvider;
import software.xdev.sse.vaadin.xhrreload.config.XHRReloadConfig;


@ConditionalOnProperty(value = "sse.vaadin.oauth2.enabled", matchIfMissing = true)
@ConditionalOnClass(OAuth2RefreshReloadCommunicator.class)
@AutoConfiguration
public class VaadinOAuth2AutoConfig
{
	@ConditionalOnBean(XHRReloadConfig.class)
	@ConditionalOnMissingBean
	@Bean
	public VaadinOAuth2RefreshReloadCommunicator vaadinOAuth2RefreshReloadCommunicator(final XHRReloadConfig config)
	{
		return new VaadinOAuth2RefreshReloadCommunicator(config);
	}
	
	@ConditionalOnMissingBean
	@Bean
	public VaadinOAuth2RefreshCommunicatiorAllowedSourcesProvider vaadinOAuth2RefreshComAllowedSourcesProvider()
	{
		return new DefaultVaadinOAuth2RefreshCommunicatiorAllowedSourcesProvider();
	}
	
	@ConditionalOnBean(OAuth2LoginLogoutPathsProvider.class)
	@ConditionalOnMissingBean
	@Bean
	public OAuth2LoginLogoutCSRFDisableRequestMatcherProvider oAuth2LoginLogoutCSRFDisableRequestMatcherProvider(
		final OAuth2LoginLogoutPathsProvider provider)
	{
		return new OAuth2LoginLogoutCSRFDisableRequestMatcherProvider(provider);
	}
}
