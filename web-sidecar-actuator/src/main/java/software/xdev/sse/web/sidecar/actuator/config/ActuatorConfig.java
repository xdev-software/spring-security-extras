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
