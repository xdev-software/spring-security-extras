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
package software.xdev.sse.web.hsts.auto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.web.hsts.DefaultHstsApplier;
import software.xdev.sse.web.hsts.HstsApplier;
import software.xdev.sse.web.hsts.HstsConfig;


@ConditionalOnProperty(value = "sse.web.hsts.auto-config.enabled", matchIfMissing = true)
@AutoConfiguration
public class HstsAutoConfig
{
	@ConfigurationProperties("sse.web.hsts")
	@ConditionalOnMissingBean
	@Bean
	public HstsConfig sseWebHstsConfig()
	{
		return new HstsConfig();
	}
	
	@ConditionalOnMissingBean
	@Bean
	public HstsApplier hstsApplier(
		final HstsConfig config,
		@Autowired(required = false) final Ssl ssl)
	{
		return new DefaultHstsApplier(config, ssl);
	}
}
