package software.xdev.sse.demo.init;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.stereotype.Component;

import software.xdev.sse.demo.persistence.FlywayInfo;
import software.xdev.sse.demo.persistence.FlywayMigration;


@Component
public class DemoFlywayConfigurationCustomizer implements FlywayConfigurationCustomizer
{
	@Override
	public void customize(final FluentConfiguration configuration)
	{
		new FlywayMigration().applyBaseConfig(configuration);
		
		configuration.locations(FlywayInfo.FLYWAY_LOOKUP_STRUCTURE);
	}
}
