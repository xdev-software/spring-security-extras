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
package software.xdev.sse.vaadin.xhrreload;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.communication.IndexHtmlRequestListener;

import software.xdev.sse.vaadin.xhrreload.config.XHRReloadConfig;


public class XHRReloadVaadinServiceInitListener implements VaadinServiceInitListener
{
	private static final Logger LOG = LoggerFactory.getLogger(XHRReloadVaadinServiceInitListener.class);
	
	protected final String scriptContents;
	
	public XHRReloadVaadinServiceInitListener(final XHRReloadConfig config)
	{
		try(final InputStream is = this.getClass().getResourceAsStream(config.getResourceLocation()))
		{
			if(is == null)
			{
				throw new IllegalStateException("Failed to find " + config.getResourceLocation());
			}
			
			// Remove comments
			this.scriptContents = Pattern.compile(
					"\\/\\*[\\s\\S]*?\\*\\/|(?<=[^:])\\/\\/.*|^\\/\\/.*")
				.matcher(new String(is.readAllBytes()))
				.replaceAll("")
				.trim();
		}
		catch(final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
	
	@Override
	public void serviceInit(final ServiceInitEvent event)
	{
		event.addIndexHtmlRequestListener((IndexHtmlRequestListener)resp -> {
			final Document document = resp.getDocument();
			
			final Element body = document.body();
			body.prependElement("script")
				.attr("type", "text/javascript")
				.html(this.scriptContents);
		});
		
		LOG.debug("Applied serviceInit");
	}
}
