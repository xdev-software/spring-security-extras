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
package software.xdev.sse.oauth2.debug;

import java.io.IOException;
import java.lang.reflect.Field;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;


public class LoggingAuthFailureHandler implements AuthenticationFailureHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(LoggingAuthFailureHandler.class);
	
	protected final AuthenticationFailureHandler original;
	
	public LoggingAuthFailureHandler(final AuthenticationFailureHandler original)
	{
		this.original = original;
	}
	
	@Override
	public void onAuthenticationFailure(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final AuthenticationException exception) throws IOException, ServletException
	{
		LOG.debug("Authentication failed", exception);
		this.original.onAuthenticationFailure(request, response, exception);
	}
	
	public static void installIfLogDebug(final OAuth2LoginConfigurer<?> c)
	{
		if(LOG.isDebugEnabled())
		{
			install(c);
		}
	}
	
	public static void install(final OAuth2LoginConfigurer<?> c)
	{
		c.addObjectPostProcessor(new ObjectPostProcessor<OAuth2LoginAuthenticationFilter>()
		{
			@Override
			public <O extends OAuth2LoginAuthenticationFilter> O postProcess(final O filter)
			{
				LoggingAuthFailureHandler.install(filter);
				return filter;
			}
		});
	}
	
	public static void install(final OAuth2LoginAuthenticationFilter filter)
	{
		try
		{
			final Field fFailureHandler =
				AbstractAuthenticationProcessingFilter.class.getDeclaredField("failureHandler");
			fFailureHandler.setAccessible(true);
			final AuthenticationFailureHandler originalHandler =
				(AuthenticationFailureHandler)fFailureHandler.get(filter);
			if(originalHandler == null)
			{
				LOG.warn("Failed to install: originalHandler is null!");
				return;
			}
			filter.setAuthenticationFailureHandler(new LoggingAuthFailureHandler(originalHandler));
		}
		catch(final Exception ex)
		{
			LOG.error("Failed to install", ex);
		}
	}
}
