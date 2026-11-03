package software.xdev.sse.demo;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import software.xdev.sse.demo.persistence.config.DefaultJPAConfig;
import software.xdev.sse.demo.persistence.util.DisableHibernateFormatMapper;


@Configuration
@EnableTransactionManagement
@EntityScan(DefaultJPAConfig.ENTITY_PACKAGE)
public class DemoJPAConfig extends DefaultJPAConfig implements HibernatePropertiesCustomizer
{
	protected Set<String> propertiesToPatch()
	{
		return Set.of("hibernate.boot.allow_jdbc_metadata_access");
	}
	
	// See also: https://stackoverflow.com/a/66220157
	@Override
	public void customize(final Map<String, Object> hibernateProperties)
	{
		this.propertiesToPatch()
			.stream()
			.collect(Collectors.toMap(p -> p.replace('_', '.'), Function.identity()))
			.forEach((target, replace) ->
				Optional.ofNullable(hibernateProperties.get(target))
					.ifPresent(value -> {
						hibernateProperties.remove(target);
						hibernateProperties.put(replace, value);
					}));
		
		hibernateProperties.putAll(DisableHibernateFormatMapper.properties());
	}
}
