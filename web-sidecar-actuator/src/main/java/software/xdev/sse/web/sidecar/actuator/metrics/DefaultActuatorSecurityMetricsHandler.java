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

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import software.xdev.sse.metrics.DefaultMetricsHandler;
import software.xdev.sse.web.sidecar.actuator.config.ActuatorSecurityConfig;


public class DefaultActuatorSecurityMetricsHandler extends DefaultMetricsHandler
	implements ActuatorSecurityMetricsHandler
{
	protected final Counter loginSuccess;
	protected final Counter loginFailed;
	
	public DefaultActuatorSecurityMetricsHandler(final ActuatorSecurityConfig config, final MeterRegistry registry)
	{
		super(config.isDefaultMetricsEnabled());
		
		if(!this.enabled())
		{
			this.loginSuccess = null;
			this.loginFailed = null;
			return;
		}
		
		final String name = PREFIX + "auth_actuator_login";
		
		this.loginSuccess = registry.counter(name, TAG_OUTCOME, "success");
		this.loginFailed = registry.counter(name, TAG_OUTCOME, "failed");
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
