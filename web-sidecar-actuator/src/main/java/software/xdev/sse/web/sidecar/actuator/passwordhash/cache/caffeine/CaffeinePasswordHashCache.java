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
package software.xdev.sse.web.sidecar.actuator.passwordhash.cache.caffeine;

import java.util.function.Function;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import software.xdev.sse.web.sidecar.actuator.passwordhash.cache.PasswordHashCache;


public class CaffeinePasswordHashCache implements PasswordHashCache
{
	protected final Cache<String, String> cache;
	
	public CaffeinePasswordHashCache(final CaffeinePasswordHashCacheConfig config)
	{
		this.cache = Caffeine.newBuilder()
			.softValues()
			.maximumSize(config.getMaxSize())
			.build();
	}
	
	@Override
	public String computeIfAbsent(final String input, final Function<String, String> compute)
	{
		return this.cache.get(input, compute);
	}
}
