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
package software.xdev.sse.web.sidecar;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;


/**
 * Manages paths that are not covered by the main/default app security
 */
public class OtherWebSecurityPaths
{
	protected final List<OtherWebSecurityPathsProvider> pathsProviders;
	
	public OtherWebSecurityPaths(final List<OtherWebSecurityPathsProvider> pathsProviders)
	{
		this.pathsProviders = pathsProviders;
	}
	
	public Set<String> all(final boolean withLogin)
	{
		return this.pathsProviders.stream()
			.filter(OtherWebSecurityPathsProvider::enabled)
			.map(p -> p.paths(withLogin))
			.flatMap(Collection::stream)
			.collect(Collectors.toSet());
	}
	
	public OrRequestMatcher requestMatcher(final boolean withLogin)
	{
		return new OrRequestMatcher(this.all(withLogin).stream()
			.map(PathPatternRequestMatcher.withDefaults()::matcher)
			.map(RequestMatcher.class::cast)
			.toList());
	}
}
