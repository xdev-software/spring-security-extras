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
package jakarta.servlet.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;


public class DummyHttpServletRequest implements HttpServletRequest
{
	@Override
	public String getAuthType()
	{
		return "";
	}
	
	@Override
	public Cookie[] getCookies()
	{
		return new Cookie[0];
	}
	
	@Override
	public long getDateHeader(final String name)
	{
		return 0;
	}
	
	@Override
	public String getHeader(final String name)
	{
		return "";
	}
	
	@Override
	public Enumeration<String> getHeaders(final String name)
	{
		return null;
	}
	
	@Override
	public Enumeration<String> getHeaderNames()
	{
		return null;
	}
	
	@Override
	public int getIntHeader(final String name)
	{
		return 0;
	}
	
	@Override
	public String getMethod()
	{
		return "";
	}
	
	@Override
	public String getPathInfo()
	{
		return "";
	}
	
	@Override
	public String getPathTranslated()
	{
		return "";
	}
	
	@Override
	public String getContextPath()
	{
		return "";
	}
	
	@Override
	public String getQueryString()
	{
		return "";
	}
	
	@Override
	public String getRemoteUser()
	{
		return "";
	}
	
	@Override
	public boolean isUserInRole(final String role)
	{
		return false;
	}
	
	@Override
	public Principal getUserPrincipal()
	{
		return null;
	}
	
	@Override
	public String getRequestedSessionId()
	{
		return "";
	}
	
	@Override
	public String getRequestURI()
	{
		return "";
	}
	
	@Override
	public StringBuffer getRequestURL()
	{
		return null;
	}
	
	@Override
	public String getServletPath()
	{
		return "";
	}
	
	@Override
	public HttpSession getSession(final boolean create)
	{
		return null;
	}
	
	@Override
	public HttpSession getSession()
	{
		return null;
	}
	
	@Override
	public String changeSessionId()
	{
		return "";
	}
	
	@Override
	public boolean isRequestedSessionIdValid()
	{
		return false;
	}
	
	@Override
	public boolean isRequestedSessionIdFromCookie()
	{
		return false;
	}
	
	@Override
	public boolean isRequestedSessionIdFromURL()
	{
		return false;
	}
	
	@Override
	public boolean authenticate(final HttpServletResponse response) throws IOException, ServletException
	{
		return false;
	}
	
	@Override
	public void login(final String username, final String password) throws ServletException
	{
	
	}
	
	@Override
	public void logout() throws ServletException
	{
	
	}
	
	@Override
	public Collection<Part> getParts() throws IOException, ServletException
	{
		return List.of();
	}
	
	@Override
	public Part getPart(final String name) throws IOException, ServletException
	{
		return null;
	}
	
	@Override
	public <T extends HttpUpgradeHandler> T upgrade(final Class<T> httpUpgradeHandlerClass)
		throws IOException, ServletException
	{
		return null;
	}
	
	@Override
	public Object getAttribute(final String name)
	{
		return null;
	}
	
	@Override
	public Enumeration<String> getAttributeNames()
	{
		return null;
	}
	
	@Override
	public String getCharacterEncoding()
	{
		return "";
	}
	
	@Override
	public void setCharacterEncoding(final String encoding) throws UnsupportedEncodingException
	{
	
	}
	
	@Override
	public int getContentLength()
	{
		return 0;
	}
	
	@Override
	public long getContentLengthLong()
	{
		return 0;
	}
	
	@Override
	public String getContentType()
	{
		return "";
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException
	{
		return null;
	}
	
	@Override
	public String getParameter(final String name)
	{
		return "";
	}
	
	@Override
	public Enumeration<String> getParameterNames()
	{
		return null;
	}
	
	@Override
	public String[] getParameterValues(final String name)
	{
		return new String[0];
	}
	
	@Override
	public Map<String, String[]> getParameterMap()
	{
		return Map.of();
	}
	
	@Override
	public String getProtocol()
	{
		return "";
	}
	
	@Override
	public String getScheme()
	{
		return "";
	}
	
	@Override
	public String getServerName()
	{
		return "";
	}
	
	@Override
	public int getServerPort()
	{
		return 0;
	}
	
	@Override
	public BufferedReader getReader() throws IOException
	{
		return null;
	}
	
	@Override
	public String getRemoteAddr()
	{
		return "";
	}
	
	@Override
	public String getRemoteHost()
	{
		return "";
	}
	
	@Override
	public void setAttribute(final String name, final Object o)
	{
	
	}
	
	@Override
	public void removeAttribute(final String name)
	{
	
	}
	
	@Override
	public Locale getLocale()
	{
		return null;
	}
	
	@Override
	public Enumeration<Locale> getLocales()
	{
		return null;
	}
	
	@Override
	public boolean isSecure()
	{
		return false;
	}
	
	@Override
	public RequestDispatcher getRequestDispatcher(final String path)
	{
		return null;
	}
	
	@Override
	public int getRemotePort()
	{
		return 0;
	}
	
	@Override
	public String getLocalName()
	{
		return "";
	}
	
	@Override
	public String getLocalAddr()
	{
		return "";
	}
	
	@Override
	public int getLocalPort()
	{
		return 0;
	}
	
	@Override
	public ServletContext getServletContext()
	{
		return null;
	}
	
	@Override
	public AsyncContext startAsync() throws IllegalStateException
	{
		return null;
	}
	
	@Override
	public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse)
		throws IllegalStateException
	{
		return null;
	}
	
	@Override
	public boolean isAsyncStarted()
	{
		return false;
	}
	
	@Override
	public boolean isAsyncSupported()
	{
		return false;
	}
	
	@Override
	public AsyncContext getAsyncContext()
	{
		return null;
	}
	
	@Override
	public DispatcherType getDispatcherType()
	{
		return null;
	}
	
	@Override
	public String getRequestId()
	{
		return "";
	}
	
	@Override
	public String getProtocolRequestId()
	{
		return "";
	}
	
	@Override
	public ServletConnection getServletConnection()
	{
		return null;
	}
}
