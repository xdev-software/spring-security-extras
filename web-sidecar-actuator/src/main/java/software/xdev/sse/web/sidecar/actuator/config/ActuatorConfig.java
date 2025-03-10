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
package software.xdev.sse.web.sidecar.actuator.config;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;


@Validated
public class ActuatorConfig
{
	@NotBlank
	private String defaultRoleName = "ACTUATOR";
	
	@Min(1)
	private int passwordMaxLength = 200;
	
	@NotNull
	private Set<ActuatorUserConfig> users = new HashSet<>();
	
	private boolean defaultMetricsEnabled = true;
	
	public String getDefaultRoleName()
	{
		return this.defaultRoleName;
	}
	
	public void setDefaultRoleName(final String defaultRoleName)
	{
		this.defaultRoleName = defaultRoleName;
	}
	
	public void setPasswordMaxLength(final int passwordMaxLength)
	{
		this.passwordMaxLength = passwordMaxLength;
	}
	
	public int getPasswordMaxLength()
	{
		return this.passwordMaxLength;
	}
	
	public Set<software.xdev.sse.web.sidecar.actuator.config.ActuatorUserConfig> getUsers()
	{
		return this.users;
	}
	
	public void setUsers(final Set<software.xdev.sse.web.sidecar.actuator.config.ActuatorUserConfig> users)
	{
		this.users = users;
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
		return "ActuatorConfig ["
			+ "defaultRoleName="
			+ this.defaultRoleName
			+ ", passwordMaxLength="
			+ this.passwordMaxLength
			+ ", users="
			+ this.users
			+ ", defaultMetricsEnabled="
			+ this.defaultMetricsEnabled
			+ "]";
	}
}
