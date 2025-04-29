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
package software.xdev.sse.oauth2.filter.deauth;

import java.util.Optional;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class DefaultDeAuthApplier implements DeAuthApplier
{
	@Override
	public void deAuth(final ServletRequest request, final ServletResponse response, final Authentication auth)
	{
		SecurityContextHolder.getContext().setAuthentication(null);
		
		Optional.ofNullable(RequestContextHolder.getRequestAttributes())
			.filter(ServletRequestAttributes.class::isInstance)
			.map(ServletRequestAttributes.class::cast)
			.ifPresent(a -> new SecurityContextLogoutHandler().logout(
				a.getRequest(),
				a.getResponse(),
				auth));
	}
}
