package software.xdev.sse.web.sidecar.actuator.config;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class ActuatorUserConfig
{
	@NotBlank
	private String username;
	
	@NotBlank
	private String passwordSha256;
	
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
	
	public String getPasswordSha256()
	{
		return this.passwordSha256;
	}
	
	public void setPasswordSha256(final String passwordSha256)
	{
		this.passwordSha256 = passwordSha256;
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
			+ ", passwordSha256="
			+ "***"
			+ ", allowedEndpoints="
			+ (this.allowedEndpoints.isEmpty() ? "<ALL>" : this.allowedEndpoints)
			+ "]";
	}
}
