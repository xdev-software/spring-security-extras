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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import software.xdev.sse.oauth2.sidecar.compat.OtherWebSecurityPathsCompat;
import software.xdev.sse.oauth2.util.DynamicLazyBeanProvider;


@ConditionalOnProperty(value = "sse.oauth2.refresh-filter.enabled", matchIfMissing = true)
@AutoConfiguration
@AutoConfigureAfter({OAuth2AuthCheckerAutoConfig.class})
public class OAuth2RefreshFilterAutoConfig
{
	private static final Logger LOG = LoggerFactory.getLogger(OAuth2RefreshFilterAutoConfig.class);
	
	@ConditionalOnMissingBean
	@Bean
	public OAuth2RefreshFilter oAuth2RefreshFilter(
		final OAuth2RefreshFilterAuthCheckMetrics metrics,
		// Some injections need to be lazy for connectionless start
		@Lazy final OAuth2AuthorizedClientService clientService,
		@Lazy final OAuth2AuthChecker oAuth2AuthChecker,
		@Autowired(required = false) final OtherWebSecurityPathsCompat otherWebSecurityPaths,
		final ApplicationContext context
	)
	{
		final OAuth2RefreshFilter filter = new OAuth2RefreshFilter(
			metrics,
			clientService,
			oAuth2AuthChecker,
			new DynamicLazyBeanProvider<>(context, OAuth2RefreshHandler.class),
			new DynamicLazyBeanProvider<>(context, OAuth2RefreshReloadCommunicator.class));
		
		if(otherWebSecurityPaths != null)
		{
			filter.setIgnoreRequestMatcher(otherWebSecurityPaths.requestMatcher(true));
			LOG.debug(
				"Automatically used {} for {}#setIgnoreRequestMatcher",
				otherWebSecurityPaths.getClass().getSimpleName(),
				filter.getClass().getSimpleName());
		}
		else
		{
			LOG.debug(
				"Nothing found to automatically configure {}#setIgnoreRequestMatcher",
				filter.getClass().getSimpleName());
		}
		
		return filter;
	}
	
	@ConditionalOnMissingBean
	@Bean
	public OAuth2RefreshFilterAuthCheckMetrics oAuth2RefreshFilterAuthCheckMetrics(final MeterRegistry meterRegistry)
	{
		return new DefaultOAuth2RefreshFilterAuthCheckMetrics(meterRegistry);
	}
}
