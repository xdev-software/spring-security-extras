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
package software.xdev.sse.oauth2.rememberloginproviderredirect.config;

import java.time.Duration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class CookieBasedRememberOAuth2LoginProviderConfig
{
	private boolean enabled = true;
	@NotBlank
	private String cookieName = "LOALP"; // Last OAuth2 Login Provider
	@NotNull
	private Duration expiration = Duration.ofDays(365);
	private String authorizationRequestBaseUri;
	
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	public void setEnabled(final boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public String getCookieName()
	{
		return this.cookieName;
	}
	
	public void setCookieName(final String cookieName)
	{
		this.cookieName = cookieName;
	}
	
	public Duration getExpiration()
	{
		return this.expiration;
	}
	
	public void setExpiration(final Duration expiration)
	{
		this.expiration = expiration;
	}
	
	public String getAuthorizationRequestBaseUri()
	{
		return this.authorizationRequestBaseUri;
	}
	
	public void setAuthorizationRequestBaseUri(final String authorizationRequestBaseUri)
	{
		this.authorizationRequestBaseUri = authorizationRequestBaseUri;
	}
	
	@Override
	public String toString()
	{
		return "CookieBasedRememberOAuth2LoginProviderConfig ["
			+ "enabled="
			+ this.enabled
			+ ", cookieName='"
			+ this.cookieName
			+ "', expiration="
			+ this.expiration
			+ ", authorizationRequestBaseUri="
			+ this.authorizationRequestBaseUri
			+ "]";
	}
}
