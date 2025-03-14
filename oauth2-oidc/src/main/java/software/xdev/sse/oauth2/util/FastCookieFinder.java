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
package software.xdev.sse.oauth2.util;

import java.util.Optional;
import java.util.stream.Stream;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


public final class FastCookieFinder
{
	public static Optional<Cookie> findCookie(final HttpServletRequest request, final String cookieName)
	{
		return Optional.ofNullable(request.getHeader("Cookie"))
			// Quick check if value is present
			.filter(h -> h.contains(cookieName))
			.flatMap(x -> Stream.of(request.getCookies())
				.filter(c -> cookieName.equals(c.getName()))
				.findFirst());
	}
	
	private FastCookieFinder()
	{
	}
}
