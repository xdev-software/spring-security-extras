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
package software.xdev.sse.vaadin;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.ApplicationServletRegistration;
import org.apache.catalina.core.StandardWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import com.vaadin.flow.router.RouteBaseData;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.spring.security.VaadinDefaultRequestCache;


/**
 * Same as {@link VaadinDefaultRequestCache}, however only existing Vaadin routes are cached, which results in no
 * invalid redirects (to e.g. PWA offline resources) and unused/useless (redirect-)sessions
 */
@SuppressWarnings("java:S6813")
@Component
public class SecureVaadinRequestCache extends VaadinDefaultRequestCache
{
	private static final Logger LOG = LoggerFactory.getLogger(SecureVaadinRequestCache.class);
	
	protected static final RequestMatcher NONE_REQUEST_MATCHER = r -> false;
	
	@Autowired
	protected ServletContext context;
	
	// Shortcut to save computation cost (no path is longer than this)
	protected int defaultPathMaxLength = 255;
	protected int defaultWildcardPathLengthAssumption = 48;
	protected int pathMaxLength = this.defaultPathMaxLength;
	protected RequestMatcher allowedMatcher;
	
	@Override
	public void saveRequest(final HttpServletRequest request, final HttpServletResponse response)
	{
		if(!HttpMethod.GET.matches(request.getMethod())
			|| request.getServletPath().length() > this.pathMaxLength
			|| !this.getAllowedPathsRequestMatcher().matches(request))
		{
			return;
		}
		
		super.saveRequest(request, response);
	}
	
	public void setPathMaxLength(final int pathMaxLength)
	{
		this.pathMaxLength = pathMaxLength;
	}
	
	public void setDefaultPathMaxLength(final int defaultPathMaxLength)
	{
		this.defaultPathMaxLength = defaultPathMaxLength;
	}
	
	public void setDefaultWildcardPathLengthAssumption(final int defaultWildcardPathLengthAssumption)
	{
		this.defaultWildcardPathLengthAssumption = defaultWildcardPathLengthAssumption;
	}
	
	protected RequestMatcher getAllowedPathsRequestMatcher()
	{
		if(this.allowedMatcher == null)
		{
			this.initAllowedPaths();
			// Failed to init (VaadinServlet maybe not initialized?)
			if(this.allowedMatcher == null)
			{
				return NONE_REQUEST_MATCHER;
			}
		}
		return this.allowedMatcher;
	}
	
	@SuppressWarnings({"java:S1075", "java:S3011"})
	protected synchronized void initAllowedPaths()
	{
		if(this.allowedMatcher != null)
		{
			return;
		}
		
		if(!(this.context.getServletRegistration("springServlet")
			instanceof final ApplicationServletRegistration applicationServletRegistration))
		{
			LOG.warn("Unable to find ApplicationServletRegistration");
			return;
		}
		
		final Wrapper wrapper;
		try
		{
			final Field fWrapper = ApplicationServletRegistration.class.getDeclaredField("wrapper");
			fWrapper.setAccessible(true);
			wrapper = (Wrapper)fWrapper.get(applicationServletRegistration);
		}
		catch(final Exception e)
		{
			LOG.error("Failed to get Wrapper", e);
			this.allowedMatcher = NONE_REQUEST_MATCHER;
			return;
		}
		
		if(!(wrapper instanceof final StandardWrapper standardWrapper)
			|| !(standardWrapper.getServlet() instanceof final VaadinServlet vaadinServlet))
		{
			LOG.warn("Unable to extract VaadinServlet from Wrapper");
			return;
		}
		
		final VaadinServletService servletService = vaadinServlet.getService();
		if(servletService == null)
		{
			LOG.info("No servletService in servlet - Not initialized yet?");
			return;
		}
		
		final Set<String> allowedPaths = servletService
			.getRouter()
			.getRegistry()
			.getRegisteredRoutes()
			.stream()
			.map(RouteBaseData::getTemplate)
			.filter(s -> !s.isBlank())
			.map(s -> s.replace("/:___url_parameter?", "/*"))
			.map(s -> "/" + s)
			.collect(Collectors.toSet());
		
		LOG.debug("Allowed paths: {}", allowedPaths);
		
		this.pathMaxLength = allowedPaths.stream()
			.mapToInt(s -> s.length() + (s.endsWith("/*") ? this.defaultWildcardPathLengthAssumption : 0))
			.max()
			.orElse(this.defaultPathMaxLength);
		
		this.allowedMatcher = new OrRequestMatcher(allowedPaths
			.stream()
			.map(AntPathRequestMatcher::new)
			.map(RequestMatcher.class::cast)
			.toList());
	}
}
