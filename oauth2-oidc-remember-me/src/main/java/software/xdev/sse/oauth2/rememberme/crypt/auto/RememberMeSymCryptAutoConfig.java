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
package software.xdev.sse.oauth2.rememberme.crypt.auto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.crypto.symmetric.manager.SymCryptManager;
import software.xdev.sse.crypto.symmetric.manager.SymCryptManagerConfig;
import software.xdev.sse.crypto.symmetric.manager.SymCryptManagerProvider;
import software.xdev.sse.oauth2.rememberme.auto.OAuth2CookieRememberMeServicesAutoConfig;
import software.xdev.sse.oauth2.rememberme.crypt.RememberMeSymCryptManager;


// Only load when configured
@ConditionalOnProperty("sse.auth.remember-me.payload-encryption.standard")
@ConditionalOnClass(SymCryptManager.class)
@AutoConfiguration
@AutoConfigureBefore(OAuth2CookieRememberMeServicesAutoConfig.class)
public class RememberMeSymCryptAutoConfig
{
	private static final Logger LOG = LoggerFactory.getLogger(RememberMeSymCryptAutoConfig.class);
	public static final String CONFIG_BEAN_NAME = "rememberMeSymCryptManagerConfig";
	
	@ConditionalOnMissingBean(name = CONFIG_BEAN_NAME)
	@Bean(name = CONFIG_BEAN_NAME)
	@ConfigurationProperties("sse.auth.remember-me.payload-encryption")
	public SymCryptManagerConfig rememberMeSymCryptManagerConfig()
	{
		return new SymCryptManagerConfig();
	}
	
	@ConditionalOnMissingBean
	@ConditionalOnBean(name = CONFIG_BEAN_NAME)
	@Bean
	public RememberMeSymCryptManager rememberMeSymCryptManager(
		final SymCryptManagerProvider symCryptManagerProvider,
		@Qualifier(CONFIG_BEAN_NAME) final SymCryptManagerConfig symCryptManagerConfig)
	{
		LOG.info("Instantiating RememberMeSymCryptManager with {}", symCryptManagerConfig);
		return new RememberMeSymCryptManagerAdapter(
			symCryptManagerProvider.createManager(symCryptManagerConfig));
	}
}
