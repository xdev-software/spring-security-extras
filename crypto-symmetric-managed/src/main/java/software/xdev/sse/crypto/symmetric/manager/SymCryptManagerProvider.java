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
package software.xdev.sse.crypto.symmetric.manager;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import software.xdev.sse.crypto.symmetric.manager.provider.SymCryptorProviderConfig;
import software.xdev.sse.crypto.symmetric.manager.provider.SymCryptorProviderInstantiator;
import software.xdev.sse.crypto.symmetric.provider.SymCryptorProvider;


public class SymCryptManagerProvider
{
	protected final List<SymCryptorProviderInstantiator<?>> instantiators;
	
	public SymCryptManagerProvider(final List<SymCryptorProviderInstantiator<?>> instantiators)
	{
		this.instantiators = instantiators;
	}
	
	public SymCryptManager createManager(final SymCryptManagerConfig config)
	{
		return this.createManager(config.getStandard(), config.symmetricCryptorProviderConfigs());
	}
	
	public SymCryptManager createManager(
		final String standard,
		final List<Map<String, ? extends SymCryptorProviderConfig>> symmetricCryptorProviderConfigs)
	{
		final Map<String, SymCryptorProvider> providers = symmetricCryptorProviderConfigs.stream()
			.flatMap(m -> m.entrySet().stream())
			// Collect to ensure that no keys are duplicated
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
			.entrySet()
			.stream()
			// Convert config to actual provider
			.map(e ->
				this.instantiators.stream()
					.filter(inst -> inst.canHandle(e.getValue()))
					.findFirst()
					.map(inst -> Map.entry(e.getKey(), inst.instantiate(e.getValue())))
					.orElse(null))
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		
		return new SymCryptManager(
			standard,
			providers.get(standard),
			providers);
	}
}
