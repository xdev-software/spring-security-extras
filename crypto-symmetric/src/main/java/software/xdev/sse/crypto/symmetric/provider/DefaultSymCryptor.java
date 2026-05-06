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
package software.xdev.sse.crypto.symmetric.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public abstract class DefaultSymCryptor implements SymCryptor
{
	protected final String cipherName;
	protected final SecretKeySpec keySpec;
	protected final AlgorithmParameterSpec parameterSpec;
	
	protected DefaultSymCryptor(
		final String cipherName,
		final SecretKeySpec keySpec,
		final AlgorithmParameterSpec parameterSpec)
	{
		this.cipherName = cipherName;
		this.keySpec = keySpec;
		this.parameterSpec = parameterSpec;
	}
	
	@Override
	public byte[] encryptBytes(final byte[] value)
	{
		try
		{
			final Cipher cipher = Cipher.getInstance(this.cipherName);
			
			cipher.init(Cipher.ENCRYPT_MODE, this.keySpec, this.parameterSpec);
			
			return cipher.doFinal(value);
		}
		catch(final IllegalBlockSizeException | BadPaddingException
					| NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
					| InvalidAlgorithmParameterException | IllegalArgumentException e)
		{
			throw new SymCryptorException(e);
		}
	}
	
	@Override
	public byte[] decryptBytes(final byte[] encrypted)
	{
		try
		{
			final Cipher cipher = Cipher.getInstance(this.cipherName);
			
			cipher.init(Cipher.DECRYPT_MODE, this.keySpec, this.parameterSpec);
			
			return cipher.doFinal(encrypted);
		}
		catch(final IllegalBlockSizeException | BadPaddingException
					| NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
					| InvalidAlgorithmParameterException | IllegalArgumentException e)
		{
			throw new SymCryptorException(e);
		}
	}
}
