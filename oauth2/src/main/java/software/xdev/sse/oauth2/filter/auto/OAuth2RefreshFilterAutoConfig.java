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
package software.xdev.sse.oauth2.filter.auto;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

import io.micrometer.core.instrument.MeterRegistry;
import software.xdev.sse.oauth2.checkauth.OAuth2AuthChecker;
import software.xdev.sse.oauth2.checkauth.auto.OAuth2AuthCheckerAutoConfig;
import software.xdev.sse.oauth2.filter.OAuth2RefreshFilter;
import software.xdev.sse.oauth2.filter.handler.OAuth2RefreshHandler;
import software.xdev.sse.oauth2.filter.metrics.DefaultOAuth2RefreshFilterAuthCheckMetrics;
import software.xdev.sse.oauth2.filter.metrics.OAuth2RefreshFilterAuthCheckMetrics;
import software.xdev.sse.oauth2.filter.reloadcom.OAuth2RefreshReloadCommunicator;
import software.xdev.sse.oauth2.util.DynamicLazyBeanProvider;
import software.xdev.sse.web.sidecar.OtherWebSecurityPaths;
import software.xdev.sse.web.sidecar.auto.CommonSidecarsAutoConfig;


@AutoConfiguration
@AutoConfigureAfter({CommonSidecarsAutoConfig.class, OAuth2AuthCheckerAutoConfig.class})
public class OAuth2RefreshFilterAutoConfig
{
	@ConditionalOnMissingBean
	@Bean
	public OAuth2RefreshFilter oAuth2RefreshFilter(
		final OAuth2RefreshFilterAuthCheckMetrics metrics,
		// Some injections need to be lazy for connectionless start
		@Lazy final OAuth2AuthorizedClientService clientService,
		@Lazy final OAuth2AuthChecker oAuth2AuthChecker,
		final OtherWebSecurityPaths otherWebSecurityPaths,
		final ApplicationContext context
	)
	{
		return new OAuth2RefreshFilter(
			metrics,
			clientService,
			oAuth2AuthChecker,
			new DynamicLazyBeanProvider<>(context, OAuth2RefreshHandler.class),
			new DynamicLazyBeanProvider<>(context, OAuth2RefreshReloadCommunicator.class))
			.setIgnoreRequestMatcher(otherWebSecurityPaths.requestMatcher(true));
	}
	
	@ConditionalOnMissingBean
	@Bean
	public OAuth2RefreshFilterAuthCheckMetrics oAuth2RefreshFilterAuthCheckMetrics(final MeterRegistry meterRegistry)
	{
		return new DefaultOAuth2RefreshFilterAuthCheckMetrics(meterRegistry);
	}
}
