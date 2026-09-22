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
package software.xdev.sse.crypto.symmetric.provider.chacha20;

import software.xdev.sse.crypto.symmetric.provider.SymCryptor;
import software.xdev.sse.crypto.symmetric.provider.SymCryptorProvider;


public class ChaCha20SymCryptorProvider implements SymCryptorProvider
{
	public static final int NONCE_LENGTH = 12;
	
	private final byte[] nonce;
	
	public ChaCha20SymCryptorProvider(final byte[] nonce)
	{
		if(nonce.length != NONCE_LENGTH)
		{
			throw new IllegalArgumentException("Nonce must be 12 bytes");
		}
		this.nonce = nonce;
	}
	
	@SuppressWarnings("checkstyle:MagicNumber")
	@Override
	public int secretKeyLength()
	{
		return 32;
	}
	
	@Override
	public SymCryptor create(final byte[] secretKey)
	{
		// In theory this can also be reversed (nonce are usually recommended to be unique)
		// However since the secretKey is more relevant as it's longer (32 byte) vs nonce (12 byte)
		return new ChaCha20SymCryptor(secretKey, this.nonce);
	}
}
