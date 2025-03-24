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
package software.xdev.sse.vaadin.oauth2.csrf;

import org.springframework.security.web.util.matcher.RequestMatcher;

import software.xdev.sse.oauth2.sidecar.OAuth2LoginLogoutPathsProvider;
import software.xdev.sse.vaadin.csrf.VaadinCSRFDisableRequestMatcherProvider;


/**
 * @implNote Fixes the following
 * <ol>
 *     <li>If disabling CSRF completely: Logout view doesn't show up and logout is performed instantly</li>
 *     <li>If using Vaadin default behavior: Creates sessions on each/rogue POST request -> pollution</li>
 * </ol>
 * <p/>
 * Solution: Only enabling it for /logout as CSRF is only required there
 */
public class OAuth2LoginLogoutCSRFDisableRequestMatcherProvider implements VaadinCSRFDisableRequestMatcherProvider
{
	private final OAuth2LoginLogoutPathsProvider oAuth2LoginLogoutPathsProvider;
	
	public OAuth2LoginLogoutCSRFDisableRequestMatcherProvider(
		final OAuth2LoginLogoutPathsProvider oAuth2LoginLogoutPathsProvider)
	{
		this.oAuth2LoginLogoutPathsProvider = oAuth2LoginLogoutPathsProvider;
	}
	
	@Override
	public RequestMatcher getMatcher()
	{
		return this.oAuth2LoginLogoutPathsProvider.logoutCSRFRequestMatcher();
	}
}
