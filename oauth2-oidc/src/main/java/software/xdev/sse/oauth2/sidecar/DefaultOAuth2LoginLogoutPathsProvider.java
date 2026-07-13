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
package software.xdev.sse.oauth2.sidecar;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


public class DefaultOAuth2LoginLogoutPathsProvider implements OAuth2LoginLogoutPathsProvider
{
	public static final String LOGIN = "/login/**";
	public static final String LOGOUT = "/logout/**";
	
	@Override
	public Set<String> paths()
	{
		return this.paths(true);
	}
	
	@Override
	public Set<String> paths(final boolean withLogin)
	{
		final Set<String> paths = new HashSet<>(Set.of(
			LOGOUT,
			OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/**"
		));
		if(withLogin)
		{
			paths.add(LOGIN);
		}
		
		return paths;
	}
	
	@Override
	public RequestMatcher logoutCSRFRequestMatcher()
	{
		return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, LOGOUT);
	}
}
