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
package software.xdev.sse.web.sidecar.actuator.auto.passwordhash.cache.expiringlimited;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.web.sidecar.actuator.auto.ActuatorWebSecurityAutoConfig;
import software.xdev.sse.web.sidecar.actuator.passwordhash.cache.PasswordHashCache;
import software.xdev.sse.web.sidecar.actuator.passwordhash.cache.expiringlimited.ExpiringLimitedPasswordHashCache;
import software.xdev.sse.web.sidecar.actuator.passwordhash.cache.expiringlimited.ExpiringLimitedPasswordHashCacheConfig;


@ConditionalOnClass(name = "software.xdev.caching.ExpiringLimitedCache")
@ConditionalOnProperty(value = "sse.sidecar.actuator.enabled", matchIfMissing = true)
@ConditionalOnProperty(
	value = "sse.sidecar.actuator.password-hash.cache.enabled",
	matchIfMissing = true)
@ConditionalOnProperty(
	value = "sse.sidecar.actuator.password-hash.cache.expiring-limited.enabled",
	matchIfMissing = true)
@AutoConfiguration
@AutoConfigureBefore(ActuatorWebSecurityAutoConfig.class)
public class ExpiringLimitedPasswordHashCacheAutoConfig
{
	@ConfigurationProperties("sse.sidecar.actuator.password-hash.cache.expiring-limited")
	@ConditionalOnMissingBean
	@Bean
	public ExpiringLimitedPasswordHashCacheConfig expiringLimitedCachedSHA256PasswordHasherConfig()
	{
		return new ExpiringLimitedPasswordHashCacheConfig();
	}
	
	@ConditionalOnMissingBean
	@Bean
	public PasswordHashCache passwordHashCache(final ExpiringLimitedPasswordHashCacheConfig config)
	{
		return new ExpiringLimitedPasswordHashCache(config);
	}
}
