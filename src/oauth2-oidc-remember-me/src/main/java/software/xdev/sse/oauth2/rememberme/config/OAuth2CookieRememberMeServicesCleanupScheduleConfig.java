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

public class OAuth2CookieRememberMeServicesCleanupScheduleConfig
{
	private int initialDelaySec = 10 * 60;
	private int fixedRateSec = 60 * 60;
	
	public int getInitialDelaySec()
	{
		return this.initialDelaySec;
	}
	
	public void setInitialDelaySec(final int initialDelaySec)
	{
		this.initialDelaySec = initialDelaySec;
	}
	
	public int getFixedRateSec()
	{
		return this.fixedRateSec;
	}
	
	public void setFixedRateSec(final int fixedRateSec)
	{
		this.fixedRateSec = fixedRateSec;
	}
	
	@Override
	public String toString()
	{
		return "OAuth2CookieRememberMeServicesCleanupScheduleConfig ["
			+ "initialDelaySec="
			+ this.initialDelaySec
			+ ", fixedRateSec="
			+ this.fixedRateSec
			+ "]";
	}
}
