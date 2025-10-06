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
package software.xdev.sse.web.sidecar.public_stateless;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import software.xdev.sse.web.sidecar.public_stateless.httpsecurity.PublicStaticStatelessHttpSecMCustomizerContainer;


@ConditionalOnProperty(value = "sse.sidecar.public-stateless.enabled", matchIfMissing = true)
@EnableWebSecurity
@AutoConfiguration
public class PublicStatelessWebSecurity
{
	private static final Logger LOG = LoggerFactory.getLogger(PublicStatelessWebSecurity.class);
	
	@Bean
	@Order(10)
	@SuppressWarnings("java:S4502")
	public SecurityFilterChain configureStaticResources(
		final PublicStaticStatelessHttpSecMCustomizerContainer httpSecurityMatcherCustomizerContainer,
		final List<PublicStatelessPathsProvider> publicStatelessPathsProviders,
		final HttpSecurity http) throws Exception
	{
		final List<String> pathPatterns = publicStatelessPathsProviders.stream()
			.filter(PublicStatelessPathsProvider::enabled)
			.map(PublicStatelessPathsProvider::paths)
			.flatMap(Collection::stream)
			.distinct()
			.toList();
		
		LOG.info("Building SecurityFilterChain using {}x path-patterns", pathPatterns.size());
		
		// Static resources that require no authentication
		return httpSecurityMatcherCustomizerContainer.apply(http, pathPatterns)
			.authorizeHttpRequests(a -> a.anyRequest().permitAll())
			// NO CSRF required as these resources are publicly available
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.build();
		
		// Possible alternative (2024):
		// Use WebMvcConfigurer#addResourceHandlers
		// registry.setOrder(1) - So that it's executed before (Vaadin)Servlet
		// spring.web.resources.add-mappings=false - Disable defaults
	}
}
