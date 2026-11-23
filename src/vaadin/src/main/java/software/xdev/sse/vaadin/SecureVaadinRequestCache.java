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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import com.vaadin.flow.spring.security.RequestUtil;
import com.vaadin.flow.spring.security.VaadinDefaultRequestCache;


/**
 * Same as {@link VaadinDefaultRequestCache}, however only existing Vaadin routes are cached, which results in no
 * invalid redirects (to e.g. PWA offline resources) and unused/useless (redirect-)sessions
 */
@SuppressWarnings("java:S6813")
@Component
public class SecureVaadinRequestCache extends VaadinDefaultRequestCache
{
	protected static final RequestMatcher NONE_REQUEST_MATCHER = r -> false;
	
	@Autowired
	protected RequestUtil requestUtil;
	
	protected RequestMatcher allowedMatcher;
	
	@Override
	public void saveRequest(final HttpServletRequest request, final HttpServletResponse response)
	{
		if(!HttpMethod.GET.matches(request.getMethod())
			|| !this.getAllowedPathsRequestMatcher().matches(request))
		{
			return;
		}
		
		super.saveRequest(request, response);
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
		
		this.allowedMatcher = this.createAllowedPathsRequestMatcher();
	}
	
	protected RequestMatcher createAllowedPathsRequestMatcher()
	{
		return this.requestUtil::isSecuredFlowRoute;
	}
}
