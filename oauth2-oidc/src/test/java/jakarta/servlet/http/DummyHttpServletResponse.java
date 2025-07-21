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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.ServletOutputStream;


@SuppressWarnings("all")
public class DummyHttpServletResponse implements HttpServletResponse
{
	@Override
	public void addCookie(final Cookie cookie)
	{
	}
	
	@Override
	public boolean containsHeader(final String name)
	{
		return false;
	}
	
	@Override
	public String encodeURL(final String url)
	{
		return "";
	}
	
	@Override
	public String encodeRedirectURL(final String url)
	{
		return "";
	}
	
	@Override
	public void sendError(final int sc, final String msg) throws IOException
	{
	}
	
	@Override
	public void sendError(final int sc) throws IOException
	{
	}
	
	@Override
	public void sendRedirect(final String location) throws IOException
	{
	}
	
	@Override
	public void setDateHeader(final String name, final long date)
	{
	}
	
	@Override
	public void addDateHeader(final String name, final long date)
	{
	}
	
	@Override
	public void setHeader(final String name, final String value)
	{
	}
	
	@Override
	public void addHeader(final String name, final String value)
	{
	}
	
	@Override
	public void setIntHeader(final String name, final int value)
	{
	}
	
	@Override
	public void addIntHeader(final String name, final int value)
	{
	}
	
	@Override
	public void setStatus(final int sc)
	{
	}
	
	@Override
	public int getStatus()
	{
		return 0;
	}
	
	@Override
	public String getHeader(final String name)
	{
		return "";
	}
	
	@Override
	public Collection<String> getHeaders(final String name)
	{
		return List.of();
	}
	
	@Override
	public Collection<String> getHeaderNames()
	{
		return List.of();
	}
	
	@Override
	public String getCharacterEncoding()
	{
		return "";
	}
	
	@Override
	public String getContentType()
	{
		return "";
	}
	
	@Override
	public ServletOutputStream getOutputStream() throws IOException
	{
		return null;
	}
	
	@Override
	public PrintWriter getWriter() throws IOException
	{
		return null;
	}
	
	@Override
	public void setCharacterEncoding(final String charset)
	{
	}
	
	@Override
	public void setContentLength(final int len)
	{
	}
	
	@Override
	public void setContentLengthLong(final long length)
	{
	}
	
	@Override
	public void setContentType(final String type)
	{
	}
	
	@Override
	public void setBufferSize(final int size)
	{
	}
	
	@Override
	public int getBufferSize()
	{
		return 0;
	}
	
	@Override
	public void flushBuffer() throws IOException
	{
	}
	
	@Override
	public void resetBuffer()
	{
	}
	
	@Override
	public boolean isCommitted()
	{
		return false;
	}
	
	@Override
	public void reset()
	{
	}
	
	@Override
	public void setLocale(final Locale loc)
	{
	}
	
	@Override
	public Locale getLocale()
	{
		return null;
	}
}
