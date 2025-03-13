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

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.micrometer.core.instrument.MeterRegistry;
import software.xdev.sse.web.sidecar.actuator.ActuatorBlackHolingPathsProvider;
import software.xdev.sse.web.sidecar.actuator.ActuatorWebSecurity;
import software.xdev.sse.web.sidecar.actuator.config.ActuatorSecurityConfig;
import software.xdev.sse.web.sidecar.actuator.metrics.ActuatorSecurityMetricsHandler;
import software.xdev.sse.web.sidecar.actuator.metrics.DefaultActuatorSecurityMetricsHandler;


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
	
	@ConditionalOnMissingBean
	@Bean
	public ActuatorSecurityMetricsHandler actuatorSecurityMetricsHandler(
		final ActuatorSecurityConfig config,
		final MeterRegistry registry)
	{
		return new DefaultActuatorSecurityMetricsHandler(config, registry);
	}
	
	@ConditionalOnProperty(value = "sse.actuator.default-black-holing.enabled", matchIfMissing = true)
	@Bean
	public ActuatorBlackHolingPathsProvider actuatorBlackHolingPathsProvider(
		final WebEndpointProperties webEndpointProperties)
	{
		return new ActuatorBlackHolingPathsProvider(webEndpointProperties);
	}
}
