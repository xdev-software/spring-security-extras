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
package software.xdev.sse.vaadin.sessioncleaner;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ConcurrentReferenceHashMap;

import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;

import software.xdev.sse.vaadin.sessioncleaner.task.VaadinSessionCleanerTask;


/**
 * Periodically "cleans" Vaadin Sessions.
 * <p>
 * Vaadin - by default - only cleans up sessions when a request for them is received.<br/> However, there are multiple
 * scenarios where a client just abruptly stops sending requests, for example:
 * <ul>
 *     <li>when network connectivity is lost</li>
 *     <li>the device entered sleep mode/was shut down</li>
 *     <li>the browser was force closed or crashed</li>
 *     <li>Mobile devices - <a href="https://developer.mozilla.org/en-US/docs/Web/API/Window/pagehide_event">MDN</a>
 *     </li>
 * </ul>
 * This might cause sessions to accumulate in memory which will result in a (kind of) memory leak.<br/>
 * The whole situation is especially problematic when there is no authentication before a
 * VaadinSession is created.
 */
public class VaadinSessionCleaner
{
	private static final Logger LOG = LoggerFactory.getLogger(VaadinSessionCleaner.class);
	
	private final Set<VaadinSession> sessions = Collections.newSetFromMap(new ConcurrentReferenceHashMap<>());
	
	private final List<VaadinSessionCleanerTask> tasks;
	
	public VaadinSessionCleaner(final List<VaadinSessionCleanerTask> tasks)
	{
		this.tasks = tasks;
		
		LOG.debug(
			"Instantiated with tasks: {}",
			tasks.stream()
				.map(VaadinSessionCleanerTask::getClass)
				.map(Class::getSimpleName)
				.toList());
	}
	
	public static final String CLEANUP_SCHEDULE_CONFIG_BEAN_NAME = "vaadinSessionCleanerScheduleConfig";
	
	@Scheduled(
		initialDelayString = "#{@" + CLEANUP_SCHEDULE_CONFIG_BEAN_NAME + ".initialDelaySec}",
		fixedRateString = "#{@" + CLEANUP_SCHEDULE_CONFIG_BEAN_NAME + ".fixedDelaySec}",
		timeUnit = TimeUnit.SECONDS)
	public void run()
	{
		LOG.debug("Starting cleanup");
		
		final long startMs = System.currentTimeMillis();
		
		// Defensive copy
		final Set<VaadinSession> vaadinSessions = new HashSet<>(this.sessions);
		for(final VaadinSession vaadinSession : vaadinSessions)
		{
			vaadinSession.lock();
			try
			{
				this.cleanUpSession(vaadinSession);
			}
			catch(final Exception ex)
			{
				LOG.warn("Failed to cleanup session[sessionId={}]", vaadinSession.getSession().getId(), ex);
			}
			finally
			{
				vaadinSession.unlock();
			}
		}
		
		LOG.debug(
			"Finishes cleanup on {}x sessions, took {}ms",
			vaadinSessions.size(),
			System.currentTimeMillis() - startMs);
	}
	
	protected void cleanUpSession(final VaadinSession vaadinSession)
	{
		final VaadinService vaadinService = vaadinSession.getService();
		if(vaadinService == null)
		{
			return;
		}
		
		for(final VaadinSessionCleanerTask task : this.tasks)
		{
			try
			{
				task.cleanUp(vaadinService, vaadinSession);
			}
			catch(final Exception ex)
			{
				LOG.warn(
					"Failed to execute cleanup task[name={}] for session[sessionId={}]",
					task.getClass().getSimpleName(),
					vaadinSession.getSession().getId(),
					ex);
			}
		}
	}
	
	public void install(final VaadinService vaadinService)
	{
		LOG.debug("Installing for {}", vaadinService);
		
		vaadinService.addSessionInitListener(ev -> this.sessions.add(ev.getSession()));
		vaadinService.addSessionDestroyListener(ev -> this.sessions.remove(ev.getSession()));
	}
}

