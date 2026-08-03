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

import java.util.Optional;

import software.xdev.sse.crypto.symmetric.manager.SymCryptManager;
import software.xdev.sse.oauth2.rememberme.crypt.RememberMeSymCryptManager;
import software.xdev.sse.oauth2.rememberme.crypt.RememberMeSymCryptorProvider;


public class RememberMeSymCryptManagerAdapter implements RememberMeSymCryptManager
{
	private final SymCryptManager manager;
	
	public RememberMeSymCryptManagerAdapter(final SymCryptManager manager)
	{
		this.manager = manager;
	}
	
	@Override
	public Optional<RememberMeSymCryptorProvider> forDecryption(final String identifier)
	{
		return this.manager.forDecryption(identifier)
			.map(RememberMeSymCryptorProviderAdapter::new);
	}
	
	@Override
	public RememberMeSymCryptorProvider forEncryption()
	{
		return new RememberMeSymCryptorProviderAdapter(this.manager.forEncryption());
	}
	
	@Override
	public String standardEncryptionProviderIdentifier()
	{
		return this.manager.standardEncryptionProviderIdentifier();
	}
}
