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
