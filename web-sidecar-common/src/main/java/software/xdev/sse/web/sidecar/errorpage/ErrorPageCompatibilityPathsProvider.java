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
package software.xdev.sse.web.sidecar.errorpage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistry;

import software.xdev.sse.web.sidecar.public_stateless.PublicStatelessPathsProvider;


/**
 * Ensures that Web-Server error pages are always accessible.
 * <p/>
 * Otherwise the pages might be blocked, resulting in confusing situations.
 */
public class ErrorPageCompatibilityPathsProvider implements PublicStatelessPathsProvider
{
	public static final String AUTO_CONFIG_ENABLE_PROPERTY = "sse.sidecar.error-page-compatibility.enabled";
	
	private static final Logger LOG = LoggerFactory.getLogger(ErrorPageCompatibilityPathsProvider.class);
	
	protected final Set<String> paths;
	
	public ErrorPageCompatibilityPathsProvider(final ErrorPageRegistry registry)
	{
		this.paths = Optional.ofNullable(registry)
			.filter(AbstractConfigurableWebServerFactory.class::isInstance)
			.map(AbstractConfigurableWebServerFactory.class::cast)
			.stream()
			.map(AbstractConfigurableWebServerFactory::getErrorPages)
			.flatMap(Collection::stream)
			.map(ErrorPage::getPath)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		
		this.printInformation();
	}
	
	protected void printInformation()
	{
		if(this.paths.isEmpty())
		{
			LOG.debug("Detected no error pages that require mitigation");
			return;
		}
		
		if(!LOG.isInfoEnabled())
		{
			return;
		}
		
		LOG.info(
			"Error pages{} are treated as stateless publicly available resource, "
				+ "as unexpected routing problems might happen otherwise! "
				+ "Therefore it's strongly recommended to disable them. "
				+ "More information is available at DEBUG log level.",
			this.paths);
		
		if(!LOG.isDebugEnabled())
		{
			return;
		}
		
		LOG.debug("Built-in error pages can be disabled with "
			+ "@Application(exclude = {ErrorMvcAutoConfiguration.class}) "
			+ "which then only shows the error page (if any) of the underlying app server.");
		LOG.debug("Alternatively you can also try to disable Spring's ErrorPageFilter "
			+ "(see https://stackoverflow.com/q/30170586 for more information).");
		LOG.debug("You can also disable this provider using '{}'", AUTO_CONFIG_ENABLE_PROPERTY);
	}
	
	@Override
	public Set<String> paths()
	{
		return this.paths;
	}
}
