package software.xdev.sse.demo.tci.db;

import java.sql.Driver;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.mariadb.jdbc.MariaDbDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.sse.demo.persistence.FlywayInfo;
import software.xdev.sse.demo.persistence.FlywayMigration;
import software.xdev.sse.demo.persistence.config.DefaultJPAConfig;
import software.xdev.sse.demo.tci.db.containers.DBContainer;
import software.xdev.tci.db.BaseDBTCI;
import software.xdev.tci.db.persistence.classfinder.DynamicPersistenceClassFinder;
import software.xdev.tci.db.persistence.hibernate.HibernateEntityManagerControllerFactory;


public class DBTCI extends BaseDBTCI<DBContainer>
{
	private static final Logger LOG = LoggerFactory.getLogger(DBTCI.class);
	
	public static final String DB_DATABASE = "test";
	public static final String DB_USERNAME = "test";
	@SuppressWarnings("java:S2068") // This is a test calm down
	public static final String DB_PASSWORD = "test";
	
	private static final DynamicPersistenceClassFinder ENTITY_CLASSES_FINDER = new DynamicPersistenceClassFinder()
		.withSearchForPersistenceClasses(DefaultJPAConfig.ENTITY_PACKAGE);
	
	public DBTCI(
		final DBContainer container,
		final String networkAlias,
		final boolean migrateAndInitializeEMC)
	{
		super(
			container,
			networkAlias,
			migrateAndInitializeEMC,
			() -> new HibernateEntityManagerControllerFactory(ENTITY_CLASSES_FINDER));
		this.withDatabase(DB_DATABASE)
			.withUsername(DB_USERNAME)
			.withPassword(DB_PASSWORD);
	}
	
	@Override
	protected void execInitialDatabaseMigration()
	{
		this.migrateDatabase(FlywayInfo.FLYWAY_LOOKUP_STRUCTURE);
	}
	
	@Override
	public void start(final String containerName)
	{
		super.start(containerName);
		if(this.migrateAndInitializeEMC)
		{
			// Do basic migrations async
			LOG.debug("Running migration to basic structure");
			this.migrateDatabase(FlywayInfo.FLYWAY_LOOKUP_STRUCTURE);
			LOG.info("Migration executed");
			
			// Create EMC in background to improve performance (~5s)
			LOG.debug("Initializing EntityManagerController...");
			this.getEMC();
			LOG.info("Initialized EntityManagerController");
		}
	}
	
	public static String getInternalJDBCUrl(final String networkAlias)
	{
		return "jdbc:mariadb://" + networkAlias + ":" + DBContainer.PORT + "/" + DB_DATABASE;
	}
	
	@Override
	protected Class<? extends Driver> driverClazz()
	{
		return org.mariadb.jdbc.Driver.class;
	}
	
	@Override
	@SuppressWarnings("java:S6437") // This is a test calm down
	public DataSource createDataSource()
	{
		final MariaDbDataSource dataSource = new MariaDbDataSource();
		try
		{
			dataSource.setUser(DB_USERNAME);
			dataSource.setPassword(DB_PASSWORD);
			dataSource.setUrl(this.getExternalJDBCUrl());
		}
		catch(final SQLException e)
		{
			throw new IllegalStateException("Invalid container setup", e);
		}
		return dataSource;
	}
	
	@Override
	public void migrateDatabase(final String... locations)
	{
		new FlywayMigration().migrate(conf ->
		{
			conf.dataSource(this.createDataSource());
			conf.locations(locations);
		});
	}
}
