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
package software.xdev.sse.crypto.symmetric.provider.aesgcm;

import software.xdev.sse.crypto.symmetric.provider.SymCryptor;
import software.xdev.sse.crypto.symmetric.provider.SymCryptorProvider;


public class AESGCMSymCryptorProvider implements SymCryptorProvider
{
	// Always 12!
	// see AESGCMSymmetricCryptor#key
	public static final int INIT_VECTOR_LENGTH = 12;
	
	/**
	 * NOTE:
	 * <p>
	 * The Security of GCM depends on the init-vector (IV) and heavily on the size of the authentication tag.
	 * Commonly a length of 12 bytes is recommended (see NIST - Special Publication 800-38D, page 21)
	 * </p>
	 * <p>
	 * <b>IV of any size</b>
	 * For GCM a 12 byte IV is strongly suggested as other IV lengths will require additional calculations. In
	 * principle any IV size can be used as long as the IV doesn't ever repeat. NIST however suggests that <b>only</b>
	 * an IV size of 12 bytes needs to be supported by implementations.
	 * </p>
	 * See also:
	 * <ul>
	 *     <li><a href="https://nvlpubs.nist.gov/nistpubs/legacy/sp/nistspecialpublication800-38d.pdf">
	 *         NIST - Special Publication 800-38D
	 *     </a></li>
	 *     <li><a href="https://crypto.stackexchange.com/q/26783">
	 *     crypto.stackexchange.com - Ciphertext and tag size and IV transmission with AES in GCM mode
	 *     </a></li>
	 *     <li><a href="https://crypto.stackexchange.com/q/41601">
	 *     crypto.stackexchange.com - AES-GCM recommended IV size: Why 12 bytes?
	 *     </a></li>
	 * </ul>
	 */
	private final byte[] initVector;
	/**
	 * Recommended: >16 or 32 (amount in byte; 1 byte = 8 bits)
	 * <p/>
	 * See also:
	 * <a href="https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html">
	 * OWASP - Cryptographic_Storage_Cheat_Sheet
	 * </a>
	 */
	private final int secretKeyLength;
	
	public AESGCMSymCryptorProvider(
		final byte[] initVector,
		final int secretKeyLength)
	{
		if(initVector.length != INIT_VECTOR_LENGTH)
		{
			throw new IllegalArgumentException("initVector must be 12 bytes");
		}
		if(secretKeyLength < 1)
		{
			throw new IllegalArgumentException("Invalid keyLength");
		}
		
		this.initVector = initVector;
		this.secretKeyLength = secretKeyLength;
	}
	
	@Override
	public int secretKeyLength()
	{
		return this.secretKeyLength;
	}
	
	@Override
	public SymCryptor create(final byte[] secretKey)
	{
		return new AESGCMSymCryptor(secretKey, this.initVector);
	}
}
