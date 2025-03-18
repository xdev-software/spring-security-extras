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
package software.xdev.sse.vaadin.oauth2;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import software.xdev.sse.oauth2.filter.reloadcom.OAuth2RefreshReloadCommunicator;
import software.xdev.sse.vaadin.xhrreload.config.XHRReloadConfig;


public class VaadinOAuth2RefreshReloadCommunicator implements OAuth2RefreshReloadCommunicator
{
	private final XHRReloadConfig config;
	
	public VaadinOAuth2RefreshReloadCommunicator(final XHRReloadConfig config)
	{
		this.config = config;
	}
	
	@Override
	public void communicate(final Source source, final ServletRequest request, final ServletResponse response)
	{
		// Redirect Vaadin's post/websocket requests otherwise UI hangs up and dies
		if(response instanceof final HttpServletResponse httpResponse)
		{
			httpResponse.setHeader(this.config.getHeader(), "1");
		}
	}
}
