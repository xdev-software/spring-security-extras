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
package software.xdev.sse.oauth2.checkauth.disabledcheck;

import java.util.Objects;
import java.util.Optional;

import jakarta.annotation.Nullable;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import software.xdev.sse.oauth2.checkauth.EmailBasedOAuth2AuthCheckerUserService;
import software.xdev.sse.oauth2.util.OAuth2AuthenticationTokenUtil;


public class DefaultEmailOAuth2IsDisabledChecker implements OAuth2IsDisabledChecker
{
	private final EmailBasedOAuth2AuthCheckerUserService userService;
	
	public DefaultEmailOAuth2IsDisabledChecker(final EmailBasedOAuth2AuthCheckerUserService userService)
	{
		this.userService = Objects.requireNonNull(userService);
	}
	
	@Override
	public boolean isDisabled(@Nullable final OAuth2AuthenticationToken token)
	{
		return Optional.ofNullable(OAuth2AuthenticationTokenUtil.getEmailAttribute(token))
			.map(this.userService::isDisabled)
			.orElse(true);
	}
}
