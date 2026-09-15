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
package software.xdev.sse.web.sidecar.auto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.error.ErrorPageRegistry;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.web.sidecar.OtherWebSecurityPaths;
import software.xdev.sse.web.sidecar.OtherWebSecurityPathsProvider;
import software.xdev.sse.web.sidecar.blackholing.BlackHolingSecurity;
import software.xdev.sse.web.sidecar.errorpage.ErrorPageCompatibilityPathsProvider;
import software.xdev.sse.web.sidecar.httpsecurity.DefaultHttpSecurityMatcherPatternApplier;
import software.xdev.sse.web.sidecar.httpsecurity.DefaultHttpSecurityMatcherPatternCreator;
import software.xdev.sse.web.sidecar.httpsecurity.HttpSecurityMatcherPatternApplier;
import software.xdev.sse.web.sidecar.httpsecurity.HttpSecurityMatcherPatternCreator;
import software.xdev.sse.web.sidecar.public_stateless.PublicStatelessWebSecurity;
import software.xdev.sse.web.sidecar.public_stateless.httpsecurity.PublicStaticStatelessHttpSecMCustomizerContainer;


@AutoConfiguration
@AutoConfigureBefore(value = {BlackHolingSecurity.class, PublicStatelessWebSecurity.class})
public class CommonSidecarsAutoConfig
{
	@ConditionalOnMissingBean
	@Bean
	public OtherWebSecurityPaths otherWebSecurityPaths(final List<OtherWebSecurityPathsProvider> pathsProviders)
	{
		return new OtherWebSecurityPaths(pathsProviders);
	}
	
	@ConditionalOnProperty(
		value = ErrorPageCompatibilityPathsProvider.AUTO_CONFIG_ENABLE_PROPERTY,
		matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public ErrorPageCompatibilityPathsProvider errorPageCompatibilityPathsProvider(
		@Autowired(required = false) final ErrorPageRegistry registry)
	{
		return new ErrorPageCompatibilityPathsProvider(registry);
	}
	
	@ConditionalOnProperty(
		value = "sse.sidecar.http-security-matcher.default.applier.enabled",
		matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public HttpSecurityMatcherPatternApplier httpSecurityMatcherPatternApplier()
	{
		return new DefaultHttpSecurityMatcherPatternApplier();
	}
	
	@ConditionalOnProperty(
		value = "sse.sidecar.http-security-matcher.default.creator.enabled",
		matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public HttpSecurityMatcherPatternCreator httpSecurityMatcherPatternCreator()
	{
		return new DefaultHttpSecurityMatcherPatternCreator();
	}
	
	@ConditionalOnProperty(
		value = "sse.sidecar.public-stateless.http-security-matcher.default.enabled",
		matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public PublicStaticStatelessHttpSecMCustomizerContainer publicStaticStatelessHttpSecMCustomizerContainer(
		final HttpSecurityMatcherPatternApplier applier,
		@Autowired(required = false) final HttpSecurityMatcherPatternCreator creator)
	{
		return new PublicStaticStatelessHttpSecMCustomizerContainer(applier, creator);
	}
}
