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
package software.xdev.sse.clientstorage.compressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;


/**
 * Uses {@link java.util.zip.Deflater} to de/compress bytes.
 * <p>
 * <b>NOTE:</b> Only efficient for larger amounts of data (usually >50 bytes)
 * </p>
 */
public class DeflateClientStorageCompressor implements ClientStorageCompressor
{
	@Override
	public byte[] compress(final byte[] input)
	{
		try(final ByteArrayOutputStream bos = new ByteArrayOutputStream())
		{
			try(final DeflaterOutputStream gos = new DeflaterOutputStream(bos))
			{
				gos.write(input);
			}
			return bos.toByteArray();
		}
		catch(final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
	
	@Override
	public byte[] decompress(final byte[] compressedInput)
	{
		try(final ByteArrayInputStream bis = new ByteArrayInputStream(compressedInput);
			final InflaterInputStream gis = new InflaterInputStream(bis))
		{
			return gis.readAllBytes();
		}
		catch(final IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}
}
