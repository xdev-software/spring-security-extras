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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import software.xdev.sse.crypto.symmetric.provider.SymCryptor;


class TestChaCha20SymCryptor
{
	static SymCryptor cryptor;
	
	@BeforeAll
	static void setUp()
	{
		cryptor = new ChaCha20SymCryptorProvider(
			new byte[]{82, 97, 110, 100, 111, 109, 73, 110, 105, 116, 73, 86})
			.create(new byte[]{
				54, 52, 50, 56, 97, 56, 102, 98, 98, 98, 48, 101, 49, 50, 48, 53,
				54, 52, 50, 56, 97, 56, 102, 98, 98, 98, 48, 101, 49, 50, 48, 53});
	}
	
	// sepp
	static final byte[] DECRYPTED = new byte[]{115, 101, 112, 112};
	static final byte[] ENCRYPTED = new byte[]{-6, 41, -48, 82};
	
	@Test
	void encrypt()
	{
		assertArrayEquals(ENCRYPTED, cryptor.encryptBytes(DECRYPTED));
	}
	
	@Test
	void decrypt()
	{
		assertArrayEquals(DECRYPTED, cryptor.decryptBytes(ENCRYPTED));
	}
}
