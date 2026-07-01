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

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.StringUtils;


/**
 * Publicly available fork of {@link org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequestUtils}
 */
public final class OidcUserRequestUtils
{
	public static boolean shouldRetrieveUserInfo(final OidcUserRequest userRequest)
	{
		// Auto-disabled if UserInfo Endpoint URI is not provided
		final ClientRegistration.ProviderDetails providerDetails =
			userRequest.getClientRegistration().getProviderDetails();
		
		return StringUtils.hasLength(providerDetails.getUserInfoEndpoint().getUri())
			&& AuthorizationGrantType.AUTHORIZATION_CODE
			.equals(userRequest.getClientRegistration().getAuthorizationGrantType());
	}
	
	private OidcUserRequestUtils()
	{
	}
}
