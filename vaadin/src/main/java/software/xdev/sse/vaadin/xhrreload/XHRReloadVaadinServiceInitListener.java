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
import java.util.Optional;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.server.communication.IndexHtmlRequestListener;

import software.xdev.sse.vaadin.xhrreload.config.XHRReloadConfig;


public class XHRReloadVaadinServiceInitListener implements VaadinServiceInitListener
{
	private static final Logger LOG = LoggerFactory.getLogger(XHRReloadVaadinServiceInitListener.class);
	
	protected final Element scriptElement;
	protected boolean scriptAttachErrorAlreadyLogged;
	
	public XHRReloadVaadinServiceInitListener(final XHRReloadConfig config)
	{
		try(final InputStream is = this.getClass().getResourceAsStream(config.getResourceLocation()))
		{
			if(is == null)
			{
				throw new IllegalStateException("Failed to find " + config.getResourceLocation());
			}
			
			// Remove comments
			final String scriptContents = Pattern.compile(
					"\\/\\*[\\s\\S]*?\\*\\/|(?<=[^:])\\/\\/.*|^\\/\\/.*")
				.matcher(new String(is.readAllBytes()))
				.replaceAll("")
				.trim();
			
			if(scriptContents.isBlank())
			{
				LOG.error("Script loaded from {} is empty", config.getResourceLocation());
			}
			
			this.scriptElement = new Element("script")
				.attr("type", "text/javascript")
				.html(scriptContents);
			LOG.trace("Built scriptElement: {}", this.scriptElement);
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
			
			try
			{
				body.prependChild(this.scriptElement.clone());
			}
			catch(final Exception ex)
			{
				LOG.atLevel(this.scriptAttachErrorAlreadyLogged
						? Level.DEBUG
						: Level.WARN)
					.setMessage(
						"Failed to attach XHRReloadScript. {}"
							+ "Details: path={} body={}")
					.addArgument(!this.scriptAttachErrorAlreadyLogged
						? "Subsequent errors will be logged at DEBUG. "
						: "")
					.addArgument(() ->
						Optional.ofNullable(resp.getVaadinRequest())
							.map(VaadinRequest::getPathInfo)
							.orElse("?"))
					.addArgument(body)
					.addArgument(ex)
					.log();
				this.scriptAttachErrorAlreadyLogged = true;
			}
		});
		
		LOG.debug("Applied serviceInit");
	}
}
