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
package software.xdev.sse.web.sidecar.httpsecurity;

import java.util.Collection;
import java.util.Objects;

import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


public class HttpSecurityMatcherCustomizerContainer
{
	private HttpSecurityMatcherPatternApplier applier;
	private HttpSecurityMatcherPatternCreator creator;
	
	public HttpSecurityMatcherCustomizerContainer(
		final HttpSecurityMatcherPatternApplier applier,
		final HttpSecurityMatcherPatternCreator creator)
	{
		this.setApplier(applier);
		this.setCreator(creator);
	}
	
	public HttpSecurityMatcherPatternApplier getApplier()
	{
		return this.applier;
	}
	
	public void setApplier(final HttpSecurityMatcherPatternApplier applier)
	{
		this.applier = Objects.requireNonNull(applier);
	}
	
	public HttpSecurityMatcherPatternCreator getCreator()
	{
		return this.creator;
	}
	
	public void setCreator(final HttpSecurityMatcherPatternCreator creator)
	{
		this.creator = creator;
	}
	
	public HttpSecurity apply(final HttpSecurity httpSecurity, final Collection<String> patterns)
	{
		final HttpSecurityMatcherPatternApplier applier = this.getApplier();
		final HttpSecurityMatcherPatternCreator creator = this.getCreator();
		LoggerFactory.getLogger(this.getClass())
			.trace("Applying to {} using {} and {}", httpSecurity, applier, creator);
		return applier.apply(httpSecurity, creator, patterns);
	}
}
