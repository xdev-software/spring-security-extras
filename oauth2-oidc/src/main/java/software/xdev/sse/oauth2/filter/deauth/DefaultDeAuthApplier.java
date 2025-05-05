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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class DefaultDeAuthApplier implements DeAuthApplier
{
	@Override
	public void deAuth(final ServletRequest request, final ServletResponse response, final Authentication auth)
	{
		// Ensure that current authentification is no longer usable
		// Better crash the application than allow unauthorized access
		SecurityContextHolder.getContext().setAuthentication(null);
		
		// Find corresponding request and response
		HttpServletRequest httpServletRequest = request instanceof final HttpServletRequest r ? r : null;
		HttpServletResponse httpServletResponse = response instanceof final HttpServletResponse r ? r : null;
		
		if(httpServletRequest == null || httpServletResponse == null)
		{
			// Fallback: Use RequestContextHolder
			final Optional<ServletRequestAttributes> optServletRequestAttributes =
				Optional.ofNullable(RequestContextHolder.getRequestAttributes())
					.filter(ServletRequestAttributes.class::isInstance)
					.map(ServletRequestAttributes.class::cast);
			if(optServletRequestAttributes.isPresent())
			{
				final ServletRequestAttributes servletRequestAttributes = optServletRequestAttributes.get();
				if(httpServletRequest == null)
				{
					httpServletRequest = servletRequestAttributes.getRequest();
				}
				if(httpServletResponse == null)
				{
					httpServletResponse = servletRequestAttributes.getResponse();
				}
			}
		}
		
		// Execute logout
		// https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html#creating-custom-logout-endpoint
		// This will invalidate the session and definitely kill the authentication
		if(httpServletRequest != null)
		{
			this.getLogoutHandler().logout(httpServletRequest, httpServletResponse, auth);
		}
	}
	
	protected LogoutHandler getLogoutHandler()
	{
		return new SecurityContextLogoutHandler();
	}
}
