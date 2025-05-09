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
package software.xdev.sse.oauth2.userenrichment;

import jakarta.annotation.Nullable;

import org.springframework.security.oauth2.core.user.OAuth2User;


public interface EnrichedOAuth2User<C> extends OAuth2User
{
	/**
	 * Returns data that was used during auth and that can be re-used on a later point e.g. for resolving session data.
	 * @apiNote The data is short-lived as it's inside a {@link java.lang.ref.SoftReference}
	 * or similar to prevent memory leaks. When the data is no longer needed {@link #clearCached()} should be called.
	 */
	@Nullable
	C getCachedAuthData();
	
	void clearCached();
}
