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

import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import software.xdev.sse.crypto.symmetric.provider.DefaultSymCryptor;


public class AESGCMSymCryptor extends DefaultSymCryptor
{
	/**
	 * See also:
	 * <ul>
	 *     <li><a href="https://find-sec-bugs.github.io/bugs.htm#ECB_MODE">FSB ECB_MODE</a></li>
	 *     <li><a href="https://find-sec-bugs.github.io/bugs.htm#PADDING_ORACLE">FSB PADDING_ORACLE</a></li>
	 *     <li><a href="https://sonarsource.atlassian.net/browse/RSPEC-4432">Sonar RSPEC-4432</a></li>
	 * </ul>
	 */
	private static final String CIPHER = "AES/GCM/NoPadding";
	
	private static final String ALGORITHM = "AES";
	
	/**
	 * <b>Size of authentication tags</b>
	 * The calculated tag will always be 16 bytes long, but the leftmost bytes can be used. GCM is defined for the tag
	 * sizes 128, 120, 112, 104, or 96, 64 and 32. Note that the security of GCM is strongly dependent on the tag size.
	 * You should try and use a tag size of 64 bits at the very minimum, but in general a tag size of the full 128 bits
	 * should be preferred.
	 * <p/>
	 * See also:
	 * <ul>
	 * <li>
	 * 	<a href="https://crypto.stackexchange.com/questions/26783/ciphertext-and-tag-size-and-iv-transmission-with-aes-in-gcm-mode">
	 * The authentication strength depends on the length of the authentication tag, like with all symmetric message
	 * authentication codes. The use of shorter authentication tags with GCM is discouraged. The bit-length of the tag,
	 * denoted t, is a security parameter. In general, t may be any one of the following five values: 128, 120, 112,
	 * 104, or 96. For certain applications, t may be 64 or 32, but the use of these two tag lengths constrains the
	 * length of the input data and the lifetime of the key. Appendix C in NIST SP 800-38D provides guidance for these
	 * constraints (for example, if t = 32 and the maximal packet size is 210 bytes, the authentication decryption
	 * function should be invoked no more than 211 times; if t = 64 and the maximal packet size is 215 bytes, the
	 * authentication decryption function should be invoked no more than 232 times).
	 *     </a>
	 * </li>
	 * <li>
	 * 	<a href="https://en.wikipedia.org/wiki/Galois/Counter_Mode#Security">
	 *     https://en.wikipedia.org/wiki/Galois/Counter_Mode#Security
	 *     </a>
	 * </li>
	 * </ul>
	 */
	private static final int GCM_TAG_LENGTH = 128;
	
	public AESGCMSymCryptor(
		final byte[] secretKey,
		final byte[] initVector)
	{
		super(
			CIPHER,
			new SecretKeySpec(secretKey, ALGORITHM),
			new GCMParameterSpec(GCM_TAG_LENGTH, initVector));
	}
}
