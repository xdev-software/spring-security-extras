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
package software.xdev.sse.web.cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultCookieSecureService implements CookieSecureService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCookieSecureService.class);
	
	/**
	 * Determines if the system uses https (e.g. behind a reverse proxy)
	 *
	 * @see <a href="https://www.baeldung.com/spring-security-session">
	 * https://www.baeldung.com/spring-security-session
	 * </a>
	 */
	private final boolean secure;
	
	public DefaultCookieSecureService(final boolean secure)
	{
		this.secure = secure;
		if(!this.secure)
		{
			LOG.info("Cookies will NOT be secured (as defined in 'server.servlet.session.cookie.secure')");
		}
	}
	
	@Override
	public boolean isSecure()
	{
		return this.secure;
	}
}
