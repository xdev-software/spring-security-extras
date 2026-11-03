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
package software.xdev.sse.oauth2.rememberme.clientstorage.auto;

import java.util.Objects;

import software.xdev.sse.clientstorage.ClientStorageProcessor;
import software.xdev.sse.oauth2.rememberme.clientstorage.RememberMeClientStorageProcessor;


public class RememberMeClientStorageProcessorAdapter implements RememberMeClientStorageProcessor
{
	private final ClientStorageProcessor clientStorageProcessor;
	
	public RememberMeClientStorageProcessorAdapter(final ClientStorageProcessor clientStorageProcessor)
	{
		this.clientStorageProcessor = Objects.requireNonNull(clientStorageProcessor);
	}
	
	@Override
	public String readValue(final String cookieValue)
	{
		return this.clientStorageProcessor.readValue(cookieValue);
	}
	
	@Override
	public String writeValue(final String value)
	{
		return this.clientStorageProcessor.writeValue(value);
	}
}
