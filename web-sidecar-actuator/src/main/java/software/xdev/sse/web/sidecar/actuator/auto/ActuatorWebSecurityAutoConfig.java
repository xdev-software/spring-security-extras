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
package software.xdev.sse.web.sidecar.actuator.auto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.micrometer.core.instrument.MeterRegistry;
import software.xdev.sse.web.sidecar.actuator.ActuatorBlackHolingPathsProvider;
import software.xdev.sse.web.sidecar.actuator.ActuatorWebSecurity;
import software.xdev.sse.web.sidecar.actuator.config.ActuatorSecurityConfig;
import software.xdev.sse.web.sidecar.actuator.httpsecurity.ActuatorHttpSecMCustomizerContainer;
import software.xdev.sse.web.sidecar.actuator.metrics.ActuatorSecurityMetricsHandler;
import software.xdev.sse.web.sidecar.actuator.metrics.DefaultActuatorSecurityMetricsHandler;
import software.xdev.sse.web.sidecar.actuator.passwordhash.cache.PasswordHashCache;
import software.xdev.sse.web.sidecar.actuator.passwordhash.cache.UnchachedPasswordHashCache;
import software.xdev.sse.web.sidecar.actuator.passwordhash.hasher.sha256.DefaultSHA256PasswordHasher;
import software.xdev.sse.web.sidecar.httpsecurity.HttpSecurityMatcherPatternApplier;
import software.xdev.sse.web.sidecar.httpsecurity.HttpSecurityMatcherPatternCreator;


@ConditionalOnProperty(value = "sse.sidecar.actuator.enabled", matchIfMissing = true)
@AutoConfiguration
@AutoConfigureBefore(ActuatorWebSecurity.class)
public class ActuatorWebSecurityAutoConfig
{
	@ConfigurationProperties("sse.actuator")
	@ConditionalOnMissingBean
	@Bean
	public ActuatorSecurityConfig actuatorConfig()
	{
		return new ActuatorSecurityConfig();
	}
	
	@ConditionalOnBean(MeterRegistry.class)
	@ConditionalOnMissingBean
	@Bean
	public ActuatorSecurityMetricsHandler actuatorSecurityMetricsHandler(
		final ActuatorSecurityConfig config,
		final MeterRegistry registry)
	{
		return new DefaultActuatorSecurityMetricsHandler(config, registry);
	}
	
	@ConditionalOnProperty(value = "sse.sidecar.actuator.default-black-holing.enabled", matchIfMissing = true)
	@Bean
	public ActuatorBlackHolingPathsProvider actuatorBlackHolingPathsProvider(
		final WebEndpointProperties webEndpointProperties)
	{
		return new ActuatorBlackHolingPathsProvider(webEndpointProperties);
	}
	
	@ConditionalOnProperty(
		value = "sse.sidecar.actuator.password-hash.hasher." + DefaultSHA256PasswordHasher.ID + ".enabled",
		matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public DefaultSHA256PasswordHasher defaultSHA256PasswordHasher()
	{
		return new DefaultSHA256PasswordHasher();
	}
	
	@ConditionalOnMissingBean
	@Bean
	public PasswordHashCache passwordHashCache()
	{
		return new UnchachedPasswordHashCache();
	}
	
	@ConditionalOnProperty(
		value = "sse.sidecar.actuator.http-security-matcher.default.enabled",
		matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public ActuatorHttpSecMCustomizerContainer actuatorHttpSecMCustomizerContainer(
		final HttpSecurityMatcherPatternApplier applier,
		@Autowired(required = false) final HttpSecurityMatcherPatternCreator creator)
	{
		return new ActuatorHttpSecMCustomizerContainer(applier, creator);
	}
}
