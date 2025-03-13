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
package software.xdev.sse.oauth2.sidecar.auto;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.oauth2.sidecar.DefaultOAuth2LoginLogoutPathsProvider;
import software.xdev.sse.oauth2.sidecar.OAuth2LoginLogoutPathsProvider;
import software.xdev.sse.web.sidecar.auto.CommonSidecarsAutoConfig;


@AutoConfiguration
@ConditionalOnClass(CommonSidecarsAutoConfig.class)
public class SidecarAutoConfig
{
	@ConditionalOnMissingBean
	@Bean
	public OAuth2LoginLogoutPathsProvider oAuth2LoginLogoutPathsProvider()
	{
		return new DefaultOAuth2LoginLogoutPathsProvider();
	}
}
