package software.xdev.sse.web.sidecar.actuator.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import software.xdev.sse.web.sidecar.actuator.config.ActuatorConfig;


@Component
public class DefaultActuatorSecurityMetricsHandler implements ActuatorSecurityMetricsHandler
{
	protected static final String OUTCOME = "outcome";
	
	protected final boolean enabled;
	
	protected final Counter loginSuccess;
	protected final Counter loginFailed;
	
	@Autowired
	public DefaultActuatorSecurityMetricsHandler(final ActuatorConfig actuatorConfig, final MeterRegistry registry)
	{
		this.enabled = actuatorConfig.isDefaultMetricsEnabled();
		
		if(!this.enabled)
		{
			this.loginSuccess = null;
			this.loginFailed = null;
			return;
		}
		
		final String name = "security_auth_actuator_login";
		
		this.loginSuccess = registry.counter(name, OUTCOME, "success");
		this.loginFailed = registry.counter(name, OUTCOME, "failed");
	}
	
	@Override
	public boolean enabled()
	{
		return this.enabled;
	}
	
	@Override
	public void loginSuccess()
	{
		this.loginSuccess.increment();
	}
	
	@Override
	public void loginFailed()
	{
		this.loginFailed.increment();
	}
}
