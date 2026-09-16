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

import software.xdev.sse.crypto.symmetric.provider.SymCryptor;
import software.xdev.sse.oauth2.rememberme.crypt.RememberMeSymCryptor;


public class RememberMeSymCryptorAdapter implements RememberMeSymCryptor
{
	private final SymCryptor cryptor;
	
	public RememberMeSymCryptorAdapter(final SymCryptor cryptor)
	{
		this.cryptor = cryptor;
	}
	
	@Override
	public byte[] encryptBytes(final byte[] value)
	{
		return cryptor.encryptBytes(value);
	}
	
	@Override
	public byte[] decryptBytes(final byte[] encrypted)
	{
		return cryptor.decryptBytes(encrypted);
	}
}
