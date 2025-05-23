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
package software.xdev.sse.oauth2.loginurl.auto;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.oauth2.loginurl.OAuth2LoginUrlStoreAdapter;
import software.xdev.sse.web.loginurl.LoginUrlStore;
import software.xdev.sse.web.loginurl.auto.LoginUrlStoreAutoConfig;


@ConditionalOnProperty(value = "sse.oauth2.oauth2-login-url-store-adapter.enabled", matchIfMissing = true)
@AutoConfiguration(after = LoginUrlStoreAutoConfig.class)
public class OAuth2LoginUrlStoreAdapterAutoConfig
{
	@ConditionalOnMissingBean
	@Bean
	@ConditionalOnBean(LoginUrlStore.class)
	public OAuth2LoginUrlStoreAdapter oAuth2LoginUrlStoreAdapter(final LoginUrlStore loginUrlStore)
	{
		return new OAuth2LoginUrlStoreAdapter(loginUrlStore);
	}
}
