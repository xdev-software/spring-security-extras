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
package software.xdev.sse.oauth2.util;

import java.util.Optional;

import jakarta.annotation.Nullable;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;


public final class OAuth2AuthenticationTokenUtil
{
	@Nullable
	public static String getEmailAttribute(final OAuth2AuthenticationToken token)
	{
		return Optional.ofNullable(token)
			.map(OAuth2AuthenticationToken::getPrincipal)
			.map(p -> p.getAttribute(StandardClaimNames.EMAIL))
			.filter(String.class::isInstance)
			.map(String.class::cast)
			.filter(s -> !s.isEmpty())
			.orElse(null);
	}
	
	private OAuth2AuthenticationTokenUtil()
	{
	}
}
