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
package software.xdev.sse.csp;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CSPGenerator
{
	protected final List<CSPProvider> allCSPProviders;
	
	public CSPGenerator(final List<CSPProvider> allCSPProviders)
	{
		this.allCSPProviders = allCSPProviders;
	}
	
	public String buildCSP()
	{
		final List<CSPProvider> overwriteValuesProviders = this.allCSPProviders.stream()
			.filter(CSPProvider::hasOverwriteValues)
			.toList();
		
		return this.collectAllValues().entrySet()
			.stream()
			.filter(e -> e.getValue() != null && !e.getValue().isEmpty())
			.map(e -> {
				Set<String> values = e.getValue();
				for(final CSPProvider provider : overwriteValuesProviders)
				{
					values = provider.overwriteValues(e.getKey(), values);
				}
				return Map.entry(e.getKey(), values);
			})
			.map(e -> e.getKey() + " " + String.join(" ", e.getValue()))
			.collect(Collectors.joining("; "));
	}
	
	protected Map<String, Set<String>> collectAllValues()
	{
		return this.allCSPProviders.stream()
			.map(CSPProvider::cspValues)
			.map(Map::entrySet)
			.flatMap(Set::stream)
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				Map.Entry::getValue,
				(v1, v2) -> Stream.of(v1, v2)
					.flatMap(Set::stream)
					.collect(Collectors.toSet())
			));
	}
}
