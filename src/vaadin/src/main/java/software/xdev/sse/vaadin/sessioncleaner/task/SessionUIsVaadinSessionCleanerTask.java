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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;

import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;


public class SessionUIsVaadinSessionCleanerTask implements VaadinSessionCleanerTask
{
	private final BiConsumer<VaadinService, VaadinSession> mVaadinServiceCleanupSession;
	
	public SessionUIsVaadinSessionCleanerTask()
	{
		this.mVaadinServiceCleanupSession = vaadinServiceCleanupSessionDelegate();
	}
	
	protected static BiConsumer<VaadinService, VaadinSession> vaadinServiceCleanupSessionDelegate()
	{
		try
		{
			final Method mCleanupSession = VaadinService.class.getDeclaredMethod(
				"cleanupSession",
				VaadinSession.class);
			mCleanupSession.setAccessible(true);
			return (service, session) -> {
				try
				{
					mCleanupSession.invoke(service, session);
				}
				catch(final IllegalAccessException | InvocationTargetException e)
				{
					throw new IllegalStateException("Failed to invoke", e);
				}
			};
		}
		catch(final NoSuchMethodException e)
		{
			throw new IllegalStateException("Failed to find method", e);
		}
	}
	
	@Override
	public void cleanUp(final VaadinService vaadinService, final VaadinSession vaadinSession)
	{
		this.mVaadinServiceCleanupSession.accept(vaadinService, vaadinSession);
	}
}
