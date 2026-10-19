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
package software.xdev.sse.codec.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class SHA256Hashing
{
	private static final String SHA_256 = "SHA-256";
	
	private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
	
	private SHA256Hashing()
	{
	}
	
	/**
	 * @apiNote Note that SHA256 gets slower the longer the input is. Ensure that the input length is limited.
	 */
	public static String hash(final String input)
	{
		if(input == null)
		{
			return null;
		}
		
		return hash(input.getBytes(StandardCharsets.UTF_8));
	}
	
	/**
	 * @apiNote Note that SHA256 gets slower the longer the input is. Ensure that the input length is limited.
	 */
	@SuppressWarnings({"checkstyle:MagicNumber", "PMD.AvoidStringBuilderOrBuffer"})
	public static String hash(final byte[] input)
	{
		if(input == null)
		{
			return null;
		}
		
		try
		{
			final MessageDigest digest = MessageDigest.getInstance(SHA_256);
			final byte[] bytes = digest.digest(input);
			
			final StringBuilder sb = new StringBuilder(2 * bytes.length);
			for(final byte b : bytes)
			{
				sb.append(HEX_DIGITS[b >> 4 & 0xf]).append(HEX_DIGITS[b & 0xf]);
			}
			return sb.toString();
		}
		// Will likely never happen
		catch(final NoSuchAlgorithmException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
