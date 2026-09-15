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
package software.xdev.sse.crypto.symmetric.manager.provider.aesgcm;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import software.xdev.sse.crypto.symmetric.manager.provider.SymCryptorProviderConfig;
import software.xdev.sse.crypto.symmetric.provider.aesgcm.AESGCMSymCryptorProvider;


public class AESGCMSymCryptorProviderConfig implements SymCryptorProviderConfig
{
	@NotNull
	@Size(
		min = AESGCMSymCryptorProvider.INIT_VECTOR_LENGTH,
		max = AESGCMSymCryptorProvider.INIT_VECTOR_LENGTH)
	private String initVector;
	
	@Min(1)
	private int secretKeyLength;
	
	public String getInitVector()
	{
		return this.initVector;
	}
	
	public void setInitVector(final String initVector)
	{
		this.initVector = initVector;
	}
	
	public int getSecretKeyLength()
	{
		return this.secretKeyLength;
	}
	
	public void setSecretKeyLength(final int secretKeyLength)
	{
		this.secretKeyLength = secretKeyLength;
	}
	
	@Override
	public String toString()
	{
		return "AESGCMSymCryptorProviderConfig ["
			+ "initVector="
			+ "***"
			+ ", secretKeyLength="
			+ this.secretKeyLength
			+ "]";
	}
}
