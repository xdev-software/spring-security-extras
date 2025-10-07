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
package software.xdev.sse.web.hsts;

import jakarta.annotation.Nullable;

import org.springframework.boot.web.server.Ssl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;


/**
 * <p>
 * Spring Boot has HSTS
 * <a href="https://docs.spring.io/spring-security/reference/features/exploits/headers.html#headers-hsts">enabled by
 * default</a> which means that it always checks if a request is secure or not. If the request is determined to be
 * secure it injects an HSTS header. This is unnecessary as HSTS is nearly always handled by the reverse proxy upstream
 * that also handles certificates.
 * </p>
 * <p>
 * Therefore HSTS is disabled when
 *     <ul>
 *         <li>it was explicitly disabled in the config</li>
 *         <li>no SSL configuration is present</li>
 *     </ul>
 * </p>
 */
public class DefaultHstsApplier implements HstsApplier
{
	protected final boolean enabled;
	
	public DefaultHstsApplier(
		final HstsConfig config,
		@Nullable final Ssl ssl)
	{
		this.enabled = !Boolean.FALSE.equals(config.isEnabled()) // true or null
			|| ssl != null && ssl.isEnabled();
	}
	
	@Override
	public HeadersConfigurer<HttpSecurity> apply(final HeadersConfigurer<HttpSecurity> c)
	{
		if(!this.enabled)
		{
			c.httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable);
		}
		return c;
	}
}
