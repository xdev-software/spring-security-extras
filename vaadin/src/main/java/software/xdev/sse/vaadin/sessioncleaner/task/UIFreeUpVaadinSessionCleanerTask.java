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
package software.xdev.sse.vaadin.sessioncleaner.task;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.internal.UIInternals;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

import software.xdev.sse.vaadin.sessioncleaner.config.UIFreeUpConfig;


public class UIFreeUpVaadinSessionCleanerTask implements VaadinSessionCleanerTask
{
	private static final Logger LOG = LoggerFactory.getLogger(UIFreeUpVaadinSessionCleanerTask.class);
	
	private final Function<VaadinService, Long> cleanAfterMsSupplier;
	
	public UIFreeUpVaadinSessionCleanerTask(final UIFreeUpConfig config)
	{
		this.cleanAfterMsSupplier = Optional.ofNullable(config.getAfterMs())
			.map(afterMs -> (Function<VaadinService, Long>)vaadinService -> afterMs)
			.orElseGet(() -> vaadinService ->
				(long)(vaadinService.getDeploymentConfiguration().getHeartbeatInterval()
					* 1000L
					* config.getAfterHeartbeatIntervalMultiplicator()));
	}
	
	@Override
	public void cleanUp(final VaadinService vaadinService, final VaadinSession vaadinSession)
	{
		// Free up last request/response data in UI internals if there was no real request for some time
		if(System.currentTimeMillis() - vaadinSession.getLastRequestTimestamp()
			> this.cleanAfterMsSupplier.apply(vaadinService))
		{
			for(final UI ui : new ArrayList<>(vaadinSession.getUIs()))
			{
				final UIInternals internals = ui.getInternals();
				if(internals.getLastRequestResponse() != null)
				{
					internals.setLastProcessedClientToServerId(
						internals.getLastProcessedClientToServerId(),
						null);
					internals.setLastRequestResponse(null);
					LOG.debug(
						"Freed up UI[id={},sessionId={}]",
						ui.getUIId(),
						vaadinSession.getSession().getId());
				}
			}
		}
	}
}
