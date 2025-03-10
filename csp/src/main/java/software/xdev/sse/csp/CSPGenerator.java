package software.xdev.sse.csp;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CSPGenerator
{
	@Autowired
	protected List<CSPProvider> allCSPProviders;
	
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
