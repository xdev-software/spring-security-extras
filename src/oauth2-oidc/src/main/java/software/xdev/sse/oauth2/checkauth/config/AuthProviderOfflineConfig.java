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
package software.xdev.sse.oauth2.checkauth.config;

import java.time.Duration;

import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;


@Validated
public class AuthProviderOfflineConfig
{
	private boolean enabled = true;
	@NotNull
	private Duration recheckInterval = Duration.ofMinutes(5);
	
	@NotNull
	private Duration maxOffline = Duration.ofHours(3);
	
	private boolean defaultMetricsEnabled = true;
	
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	public void setEnabled(final boolean enabled)
	{
		this.enabled = enabled;
	}
	
	public Duration getRecheckInterval()
	{
		return this.recheckInterval;
	}
	
	public void setRecheckInterval(final Duration recheckInterval)
	{
		this.recheckInterval = recheckInterval;
	}
	
	public Duration getMaxOffline()
	{
		return this.maxOffline;
	}
	
	public void setMaxOffline(final Duration maxOffline)
	{
		this.maxOffline = maxOffline;
	}
	
	public boolean isDefaultMetricsEnabled()
	{
		return this.defaultMetricsEnabled;
	}
	
	public void setDefaultMetricsEnabled(final boolean defaultMetricsEnabled)
	{
		this.defaultMetricsEnabled = defaultMetricsEnabled;
	}
	
	@Override
	public String toString()
	{
		return "AuthProviderOfflineConfig ["
			+ "enabled="
			+ this.enabled
			+ ", recheckInterval="
			+ this.recheckInterval
			+ ", maxOffline="
			+ this.maxOffline
			+ ", defaultMetricsEnabled="
			+ this.defaultMetricsEnabled
			+ "]";
	}
}
