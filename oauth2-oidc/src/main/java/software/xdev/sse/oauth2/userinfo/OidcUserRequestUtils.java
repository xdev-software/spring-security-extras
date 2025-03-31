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
package software.xdev.sse.oauth2.userinfo;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.util.StringUtils;


/**
 * @apiNote only contains required code for {@link OidcUserService}
 * @see org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequestUtils
 */
public final class OidcUserRequestUtils
{
	public static OidcUser getUser(final OidcUserRequest userRequest, final OidcUserInfo userInfo)
	{
		final Set<GrantedAuthority> authorities = new LinkedHashSet<>();
		final ClientRegistration.ProviderDetails providerDetails =
			userRequest.getClientRegistration().getProviderDetails();
		final String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
		if(StringUtils.hasText(userNameAttributeName))
		{
			authorities.add(new OidcUserAuthority(userRequest.getIdToken(), userInfo, userNameAttributeName));
		}
		else
		{
			authorities.add(new OidcUserAuthority(userRequest.getIdToken(), userInfo));
		}
		final OAuth2AccessToken token = userRequest.getAccessToken();
		for(final String scope : token.getScopes())
		{
			authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
		}
		if(StringUtils.hasText(userNameAttributeName))
		{
			return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName);
		}
		return new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
	}
	
	private OidcUserRequestUtils()
	{
	}
}
