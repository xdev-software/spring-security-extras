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

import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import software.xdev.sse.crypto.symmetric.provider.DefaultSymCryptor;


/**
 * Uses <a href="https://en.wikipedia.org/wiki/Salsa20#Internet_standards">ChaCha20</a>.
 * <p/>
 * According to <a href="https://openjdk.org/jeps/329">JEP-329</a>/
 * <a href="https://datatracker.ietf.org/doc/html/rfc7539">RF7539</a>:
 * <ul>
 *     <li>256 bit / 32 byte key</li>
 *     <li>96 bit / 12 byte nonce</li>
 *     <li>32 bit / 4 byte counter</li>
 * </ul>
 */
public class ChaCha20SymCryptor extends DefaultSymCryptor
{
	private static final String ALGORITHM = "ChaCha20";
	
	private static final String CIPHER = "ChaCha20";
	
	public ChaCha20SymCryptor(
		final byte[] key,
		final byte[] nonce)
	{
		super(
			CIPHER,
			new SecretKeySpec(key, ALGORITHM),
			new ChaCha20ParameterSpec(nonce, 1));
	}
}
