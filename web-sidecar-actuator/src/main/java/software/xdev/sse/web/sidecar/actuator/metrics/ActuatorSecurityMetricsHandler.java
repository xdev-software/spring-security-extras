package software.xdev.sse.web.sidecar.actuator.metrics;

public interface ActuatorSecurityMetricsHandler
{
	default boolean enabled()
	{
		return true;
	}
	
	void loginSuccess();
	
	void loginFailed();
}
