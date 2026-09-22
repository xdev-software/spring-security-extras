package software.xdev.sse.demo.persistence;

import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.output.MigrateResult;
import org.flywaydb.core.internal.license.VersionPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FlywayMigration
{
	private static final Logger LOG = LoggerFactory.getLogger(FlywayMigration.class);
	
	public void applyBaseConfig(final FluentConfiguration conf)
	{
		conf.table(FlywayInfo.FLYWAY_TABLENAME);
		
		conf.sqlMigrationPrefix(FlywayInfo.FLYWAY_MIGRATION_PREFIX);
		conf.sqlMigrationSeparator(FlywayInfo.FLYWAY_MIGRATION_SEPARATOR);
		
		conf.validateOnMigrate(false);
	}
	
	public void migrate(final Consumer<FluentConfiguration> configurator)
	{
		final FluentConfiguration config = Flyway.configure();
		this.applyBaseConfig(config);
		configurator.accept(config);
		this.migrate(config);
	}
	
	protected void migrate(final FluentConfiguration flywayConfig)
	{
		LOG.info("Starting Flyway Migration - Flyway {}", VersionPrinter.getVersion());
		
		if(LOG.isDebugEnabled())
		{
			LOG.debug("Configuration for flyway: {}", this.getConfiguration(flywayConfig));
		}
		
		LOG.debug("Loading flyway instance");
		final Flyway flyway = flywayConfig.load();
		LOG.debug("Finished loading of flyway instance");
		
		if(flyway.info().current() == null)
		{
			LOG.info("Detected fresh database instance!");
		}
		
		LOG.debug("Starting DB-Migration");
		final long migrateStartTime = System.currentTimeMillis();
		
		final MigrateResult migrationResult = flyway.migrate();
		
		LOG.info(
			"Successfully updated from {} to {} and migrated {} changes, took {}ms",
			migrationResult.initialSchemaVersion,
			migrationResult.targetSchemaVersion,
			migrationResult.migrationsExecuted,
			System.currentTimeMillis() - migrateStartTime);
		
		LOG.info("Migration Done");
	}
	
	/**
	 * Return Configuration as String for Logging
	 *
	 * @return configuration as String for Logging
	 */
	protected String getConfiguration(final FluentConfiguration conf)
	{
		return "table=" + conf.getTable()
			+ ", locations=" + Stream.of(conf.getLocations())
			.map(Location::getPath)
			.collect(Collectors.joining(","))
			+ ", migPrefix=" + conf.getSqlMigrationPrefix()
			+ ", migSeparator=" + conf.getSqlMigrationSeparator();
	}
}
