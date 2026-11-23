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
package software.xdev.sse.crypto.symmetric.manager;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import software.xdev.sse.crypto.symmetric.provider.SymCryptorProvider;


public class SymCryptManager
{
	private final String standardProviderIdentifier;
	private final SymCryptorProvider standardProvider;
	private final Map<String, SymCryptorProvider> cryptorProviders;
	
	public SymCryptManager(
		final String standardProviderIdentifier,
		final SymCryptorProvider standardProvider,
		final Map<String, SymCryptorProvider> cryptorProviders)
	{
		this.standardProviderIdentifier = Objects.requireNonNull(standardProviderIdentifier);
		this.standardProvider = Objects.requireNonNull(standardProvider);
		this.cryptorProviders = Objects.requireNonNull(cryptorProviders);
	}
	
	public Optional<SymCryptorProvider> forDecryption(final String identifier)
	{
		return Optional.ofNullable(this.cryptorProviders.get(identifier));
	}
	
	public SymCryptorProvider forEncryption()
	{
		return this.standardProvider;
	}
	
	public String standardEncryptionProviderIdentifier()
	{
		return this.standardProviderIdentifier;
	}
}
