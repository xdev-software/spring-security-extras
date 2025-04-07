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
package software.xdev.sse.clientstorage;

import java.util.Base64;
import java.util.Objects;

import software.xdev.sse.clientstorage.compressor.ClientStorageCompressor;
import software.xdev.sse.clientstorage.compressor.DeflateClientStorageCompressor;
import software.xdev.sse.crypto.symmetric.provider.SymCryptor;


/**
 * Processes client values (e.g. cookies or local storage) with
 * <ul>
 *     <li>{@link DeflateClientStorageCompressor}</li>
 *     <li>{@link SymCryptor}</li>
 *     <li>{@link Base64}</li>
 * </ul>
 * (vice versa on decode)
 */
public class DefaultClientStorageProcessor implements ClientStorageProcessor
{
	private final ClientStorageCompressor compressor;
	private final SymCryptor cryptor;
	
	public DefaultClientStorageProcessor(final ClientStorageCompressor compressor, final SymCryptor cryptor)
	{
		this.compressor = Objects.requireNonNull(compressor);
		this.cryptor = Objects.requireNonNull(cryptor);
	}
	
	@Override
	public String writeValue(final String value)
	{
		return Base64.getEncoder().encodeToString(
			this.cryptor.encryptBytes(
				this.compressor.compress(
					value.getBytes())));
	}
	
	@Override
	@SuppressWarnings("java:S2129") // Incorrect detection
	public String readValue(final String cookieValue)
	{
		return new String(
			this.compressor.decompress(
				this.cryptor.decryptBytes(
					Base64.getDecoder().decode(cookieValue))));
	}
}
