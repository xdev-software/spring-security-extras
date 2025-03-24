package software.xdev.sse.demo.tci.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.mariadb.jdbc.Driver;
import org.mariadb.jdbc.MariaDbDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;

import software.xdev.sse.demo.persistence.FlywayInfo;
import software.xdev.sse.demo.persistence.FlywayMigration;
import software.xdev.sse.demo.persistence.util.EntityManagerController;
import software.xdev.sse.demo.tci.db.containers.DBContainer;
import software.xdev.tci.TCI;


public class DBTCI extends TCI<DBContainer>
{
	private static final Logger LOG = LoggerFactory.getLogger(DBTCI.class);
	
	public static final String DB_DATABASE = "test";
	public static final String DB_USERNAME = "test";
	@SuppressWarnings("java:S2068") // This is a test calm down
	public static final String DB_PASSWORD = "test";
	
	private final boolean migrateAndInitializeEMC;
	
	protected EntityManagerController emc;
	
	public DBTCI(
		final DBContainer container,
		final String networkAlias,
		final boolean migrateAndInitializeEMC)
	{
		super(container, networkAlias);
		this.migrateAndInitializeEMC = migrateAndInitializeEMC;
	}
	
	public boolean isMigrateAndInitializeEMC()
	{
		return this.migrateAndInitializeEMC;
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
	
	@Override
	public void stop()
	{
		if(this.emc != null)
		{
			try
			{
				this.emc.close();
			}
			catch(final Exception ex)
			{
				LOG.warn("Failed to close EntityManagerController", ex);
			}
			this.emc = null;
		}
		super.stop();
	}
	
	public EntityManagerController getEMC()
	{
		if(this.emc == null)
		{
			this.initEMCIfRequired();
		}
		
		return this.emc;
	}
	
	protected synchronized void initEMCIfRequired()
	{
		if(this.emc == null)
		{
			this.emc = EntityManagerController.createForStandalone(
				Driver.class.getName(),
				// Use production-ready pool otherwise Hibernate warnings occur
				HikariCPConnectionProvider.class.getName(),
				this.getExternalJDBCUrl(),
				DB_USERNAME,
				DB_PASSWORD
			);
		}
	}
	
	public static String getInternalJDBCUrl(final String networkAlias)
	{
		return "jdbc:mariadb://" + networkAlias + ":" + DBContainer.PORT + "/" + DB_DATABASE;
	}
	
	public String getExternalJDBCUrl()
	{
		return this.getContainer().getJdbcUrl();
	}
	
	/**
	 * Creates a new {@link EntityManager} with an internal {@link EntityManagerFactory}, which can be used to load and
	 * save data in the database for the test.
	 *
	 * <p>
	 * It may be a good idea to close the EntityManager, when you're finished with it.
	 * </p>
	 * <p>
	 * All created EntityManager are automatically cleaned up when the test is finished.
	 * </p>
	 *
	 * @return EntityManager
	 */
	public EntityManager createEntityManager()
	{
		return this.getEMC().createEntityManager();
	}
	
	public void useNewEntityManager(final Consumer<EntityManager> action)
	{
		try(final EntityManager em = this.createEntityManager())
		{
			action.accept(em);
		}
	}
	
	@SuppressWarnings("java:S6437") // This is a test calm down
	public MariaDbDataSource createDataSource()
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
	
	public void migrateDatabase(final Collection<String> locations)
	{
		this.migrateDatabase(locations.toArray(String[]::new));
	}
	
	public void migrateDatabase(final String... locations)
	{
		new FlywayMigration().migrate(conf ->
		{
			conf.dataSource(this.createDataSource());
			conf.locations(locations);
		});
	}
	
	public void logDataBaseInfo()
	{
		if(Optional.ofNullable(this.getContainer()).map(GenericContainer::getContainerId).isPresent())
		{
			return;
		}
		
		final Logger logger = LoggerFactory.getLogger(DBTCI.class.getName() + ".sqlstatus");
		if(!logger.isInfoEnabled())
		{
			return;
		}
		
		logger.info("===== SERVER INFO =====");
		try(final Connection conn = this.createDataSource().getConnection();
			final Statement stmt = conn.createStatement())
		{
			logger.info("> SHOW PROCESSLIST <");
			this.printDatabaseInfo(stmt.executeQuery("SHOW PROCESSLIST"), logger);
			
			if(logger.isTraceEnabled())
			{
				logger.info("> SHOW STATUS (TRACE) <");
				this.printDatabaseInfo(stmt.executeQuery("SHOW STATUS"), logger);
			}
			else if(logger.isDebugEnabled())
			{
				logger.info("> SHOW STATUS LIKE '%onn%' (DEBUG) <");
				this.printDatabaseInfo(stmt.executeQuery("SHOW STATUS LIKE '%onn%'"), logger);
			}
		}
		catch(final Exception ex)
		{
			logger.warn("Unable to print database info", ex);
		}
		finally
		{
			logger.info("=======================");
		}
	}
	
	@SuppressWarnings("java:S2629") // is already checked when invoked
	private void printDatabaseInfo(final ResultSet rs, final Logger logger) throws SQLException
	{
		final int colCount = rs.getMetaData().getColumnCount();
		
		logger.info(
			IntStream.range(1, colCount + 1).mapToObj(id ->
			{
				try
				{
					return rs.getMetaData().getColumnName(id);
				}
				catch(final SQLException e)
				{
					return "--Exception--";
				}
			}).collect(Collectors.joining(",")));
		while(rs.next())
		{
			logger.info(
				IntStream.range(1, colCount + 1).mapToObj(id ->
				{
					try
					{
						return rs.getString(id);
					}
					catch(final SQLException e)
					{
						return "--Exception--";
					}
				}).collect(Collectors.joining(",")));
		}
	}
}
