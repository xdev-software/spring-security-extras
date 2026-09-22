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
package software.xdev.sse.crypto.symmetric.manager.provider;

import software.xdev.sse.crypto.symmetric.provider.SymCryptorProvider;


public abstract class SymCryptorProviderInstantiator<C extends SymCryptorProviderConfig>
{
	protected Class<C> configClass;
	
	protected SymCryptorProviderInstantiator(final Class<C> configClass)
	{
		this.configClass = configClass;
	}
	
	public boolean canHandle(final SymCryptorProviderConfig config)
	{
		return this.configClass.isInstance(config);
	}
	
	public abstract SymCryptorProvider instantiate(C config);
	
	@SuppressWarnings("unchecked")
	public SymCryptorProvider instantiate(final Object config)
	{
		return this.instantiate((C)config);
	}
}
