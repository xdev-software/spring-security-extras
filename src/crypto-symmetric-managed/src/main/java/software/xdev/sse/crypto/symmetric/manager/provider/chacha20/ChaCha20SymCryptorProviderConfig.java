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
package software.xdev.sse.crypto.symmetric.manager.provider.chacha20;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import software.xdev.sse.crypto.symmetric.manager.provider.SymCryptorProviderConfig;
import software.xdev.sse.crypto.symmetric.provider.chacha20.ChaCha20SymCryptorProvider;


public class ChaCha20SymCryptorProviderConfig implements SymCryptorProviderConfig
{
	@NotNull
	@Size(
		min = ChaCha20SymCryptorProvider.NONCE_LENGTH,
		max = ChaCha20SymCryptorProvider.NONCE_LENGTH)
	private String nonce;
	
	public String getNonce()
	{
		return this.nonce;
	}
	
	public void setNonce(final String nonce)
	{
		this.nonce = nonce;
	}
	
	@Override
	public String toString()
	{
		return "ChaCha20SymCyptorProviderConfig ["
			+ "nonce="
			+ "***"
			+ "]";
	}
}
