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
package software.xdev.sse.oauth2.rememberme.userenrichment;

import org.springframework.security.oauth2.core.user.OAuth2User;

import software.xdev.sse.oauth2.userenrichment.OAuth2UserEnricher;


public interface OAuth2RememberMeUserEnricher<U extends OAuth2User, C> extends OAuth2UserEnricher<U, C>
{
	OAuth2User enrichForRememberMe(OAuth2User user);
}
