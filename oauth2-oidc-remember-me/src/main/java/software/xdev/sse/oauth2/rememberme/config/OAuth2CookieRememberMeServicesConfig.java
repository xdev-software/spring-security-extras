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
package software.xdev.sse.oauth2.rememberme.config;

import java.time.Duration;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public class OAuth2CookieRememberMeServicesConfig
{
	private boolean enabled = true;
	
	@NotEmpty
	private String payloadCookieName = "AC"; // Auth Cache
	@NotEmpty
	private String idCookieName = "ACID"; // Auth Cache Identifier
	
	@NotNull
	private Duration expiration = Duration.ofDays(3);
	
	@Min(1)
	private int maxPerUser = 5;
	
	@NotNull
	private OAuth2CookieRememberMeServicesCleanupScheduleConfig cleanupSchedule =
		new OAuth2CookieRememberMeServicesCleanupScheduleConfig();
	
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	public String disabledReason()
	{
		return "manually disabled";
	}
	
	public void setEnabled(final boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public String getPayloadCookieName()
	{
		return this.payloadCookieName;
	}
	
	public void setPayloadCookieName(final String payloadCookieName)
	{
		this.payloadCookieName = payloadCookieName;
	}
	
	public String getIdCookieName()
	{
		return this.idCookieName;
	}
	
	public void setIdCookieName(final String idCookieName)
	{
		this.idCookieName = idCookieName;
	}
	
	public Duration getExpiration()
	{
		return this.expiration;
	}
	
	public void setExpiration(final Duration expiration)
	{
		this.expiration = expiration;
	}
	
	public int getMaxPerUser()
	{
		return this.maxPerUser;
	}
	
	public void setMaxPerUser(final int maxPerUser)
	{
		this.maxPerUser = maxPerUser;
	}
	
	public OAuth2CookieRememberMeServicesCleanupScheduleConfig getCleanupSchedule()
	{
		return cleanupSchedule;
	}
	
	public void setCleanupSchedule(final OAuth2CookieRememberMeServicesCleanupScheduleConfig cleanupSchedule)
	{
		this.cleanupSchedule = cleanupSchedule;
	}
	
	@Override
	public String toString()
	{
		return "OAuth2CookieRememberMeServicesConfig ["
			+ "enabled="
			+ this.enabled
			+ ", payloadCookieName='"
			+ this.payloadCookieName
			+ "', idCookieName='"
			+ this.idCookieName
			+ "', expiration="
			+ this.expiration
			+ ", maxPerUser="
			+ this.maxPerUser
			+ ", cleanupSchedule="
			+ this.cleanupSchedule
			+ "]";
	}
}
