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
package software.xdev.sse.crypto.symmetric.manager.auto;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.crypto.symmetric.manager.SymCryptManagerProvider;
import software.xdev.sse.crypto.symmetric.manager.provider.SymCryptorProviderInstantiator;
import software.xdev.sse.crypto.symmetric.manager.provider.aesgcm.AESGCMSymCryptorProviderInstantiator;
import software.xdev.sse.crypto.symmetric.manager.provider.chacha20.ChaCha20SymCryptorProviderInstantiator;


@AutoConfiguration
public class SymCryptAutoConfig
{
	@ConditionalOnProperty(
		value = "sse.sym-crypt.provider-instantiators.default-aes-gcm.enabled",
		matchIfMissing = true)
	@Bean
	public AESGCMSymCryptorProviderInstantiator aesgcmSymCryptorProviderInstantiator()
	{
		return new AESGCMSymCryptorProviderInstantiator();
	}
	
	@ConditionalOnProperty(
		value = "sse.sym-crypt.provider-instantiators.default-cha-cha-20.enabled",
		matchIfMissing = true)
	@Bean
	public ChaCha20SymCryptorProviderInstantiator chaCha20SymCryptorProviderInstantiator()
	{
		return new ChaCha20SymCryptorProviderInstantiator();
	}
	
	@ConditionalOnMissingBean
	@Bean
	public SymCryptManagerProvider symCryptManagerProvider(final List<SymCryptorProviderInstantiator<?>> instantiators)
	{
		return new SymCryptManagerProvider(instantiators);
	}
}
