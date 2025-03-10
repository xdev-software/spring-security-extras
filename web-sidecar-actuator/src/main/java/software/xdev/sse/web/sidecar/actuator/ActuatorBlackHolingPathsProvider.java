package software.xdev.sse.web.sidecar.actuator;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.stereotype.Component;

import software.xdev.sse.web.sidecar.blackholing.BlackHolingPathsProvider;


@Component
public class ActuatorBlackHolingPathsProvider implements BlackHolingPathsProvider
{
	@Autowired
	protected WebEndpointProperties actuatorWebEndpointProperties;
	
	@Override
	public Set<String> paths()
	{
		return Set.of(this.actuatorWebEndpointProperties.getBasePath() + "/**");
	}
}
