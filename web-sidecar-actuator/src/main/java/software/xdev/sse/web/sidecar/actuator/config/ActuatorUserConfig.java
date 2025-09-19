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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.slf4j.LoggerFactory;


public class ActuatorUserConfig
{
	@NotBlank
	private String username;
	
	@NotBlank
	private String passwordHash;
	
	@NotNull
	private Set<String> allowedEndpoints = new HashSet<>(); // If empty -> ACCESS TO ALL ENDPOINTS
	
	public String getUsername()
	{
		return this.username;
	}
	
	public void setUsername(final String username)
	{
		this.username = username;
	}
	
	/**
	 * @deprecated Replaced by {@link #getPasswordHash()}
	 */
	@Deprecated(since = "1.3.0", forRemoval = true)
	public String getPasswordSha256()
	{
		return this.getPasswordHash();
	}
	
	/**
	 * @deprecated Replaced by {@link #setPasswordHash(String)}
	 */
	@Deprecated(since = "1.3.0", forRemoval = true)
	public void setPasswordSha256(final String passwordSha256)
	{
		LoggerFactory.getLogger(this.getClass())
			.error("Detected usage of deprecated property 'passwordSha256' that will be removed soon."
				+ " Use 'passwordHash' instead!");
		this.setPasswordHash(passwordSha256);
	}
	
	public String getPasswordHash()
	{
		return this.passwordHash;
	}
	
	public void setPasswordHash(final String passwordHash)
	{
		this.passwordHash = passwordHash;
	}
	
	public void setAllowedEndpoints(final Set<String> allowedEndpoints)
	{
		this.allowedEndpoints = allowedEndpoints;
	}
	
	public Set<String> getAllowedEndpoints()
	{
		return this.allowedEndpoints;
	}
	
	@Override
	public String toString()
	{
		return "ActuatorUserConfig ["
			+ "username="
			+ this.username
			+ ", passwordHash="
			+ "***"
			+ ", allowedEndpoints="
			+ (this.allowedEndpoints.isEmpty() ? "<ALL>" : this.allowedEndpoints)
			+ "]";
	}
}
