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
package software.xdev.sse.web.sidecar.actuator.auto.passwordhash.cache.caffeine;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.web.sidecar.actuator.auto.ActuatorWebSecurityAutoConfig;
import software.xdev.sse.web.sidecar.actuator.passwordhash.cache.PasswordHashCache;
import software.xdev.sse.web.sidecar.actuator.passwordhash.cache.caffeine.CaffeinePasswordHashCache;
import software.xdev.sse.web.sidecar.actuator.passwordhash.cache.caffeine.CaffeinePasswordHashCacheConfig;


@ConditionalOnClass(name = "com.github.benmanes.caffeine.cache.Caffeine")
@ConditionalOnProperty(value = "sse.sidecar.actuator.enabled", matchIfMissing = true)
@ConditionalOnProperty(value = "sse.sidecar.actuator.password-hash.cache.enabled")
@ConditionalOnProperty(
	value = "sse.sidecar.actuator.password-hash.cache.caffeine.enabled",
	matchIfMissing = true)
@AutoConfiguration
@AutoConfigureBefore(ActuatorWebSecurityAutoConfig.class)
public class CaffeinePasswordHashCacheAutoConfig
{
	@ConfigurationProperties("sse.sidecar.actuator.password-hash.cache.caffeine")
	@ConditionalOnMissingBean
	@Bean
	public CaffeinePasswordHashCacheConfig caffeinePasswordHashCacheConfig()
	{
		return new CaffeinePasswordHashCacheConfig();
	}
	
	@ConditionalOnMissingBean
	@Bean
	public PasswordHashCache passwordHashCache(final CaffeinePasswordHashCacheConfig config)
	{
		return new CaffeinePasswordHashCache(config);
	}
}
