package software.xdev.sse.demo.persistence.util;

import static java.util.Map.entry;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitTransactionType;

import org.hibernate.cfg.JdbcSettings;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;

import software.xdev.sse.demo.persistence.config.DefaultJPAConfig;


/**
 * Handles the creation and destruction of {@link EntityManager}s.
 * <p/>
 * This should only be used when a {@link EntityManager} has to be created manually, e.g. when not running on an
 * AppServer.
 */
public class EntityManagerController implements AutoCloseable
{
	private static final Logger LOG = LoggerFactory.getLogger(EntityManagerController.class);
	
	protected final List<EntityManager> activeEms = Collections.synchronizedList(new ArrayList<>());
	protected final EntityManagerFactory emf;
	
	public EntityManagerController(final EntityManagerFactory emf)
	{
		this.emf = Objects.requireNonNull(emf);
	}
	
	/**
	 * Creates a new {@link EntityManager} with an internal {@link EntityManagerFactory}, which can be used to load and
	 * save data in the database.
	 *
	 * <p>
	 * It may be a good idea to close the EntityManager, when you're finished with it.
	 * </p>
	 * <p>
	 * All created EntityManager are automatically cleaned up once {@link #close()} is called.
	 * </p>
	 *
	 * @return EntityManager
	 */
	public EntityManager createEntityManager()
	{
		final EntityManager em = this.emf.createEntityManager();
		this.activeEms.add(em);
		
		return em;
	}
	
	@Override
	public void close()
	{
		LOG.debug("Shutting down resources");
		this.activeEms.forEach(em ->
		{
			try
			{
				if(em.getTransaction() != null && em.getTransaction().isActive())
				{
					em.getTransaction().rollback();
				}
				em.close();
			}
			catch(final Exception e)
			{
				LOG.warn("Unable to close EntityManager", e);
			}
		});
		
		LOG.debug("Cleared {}x EntityManagers", this.activeEms.size());
		
		this.activeEms.clear();
		
		try
		{
			this.emf.close();
			LOG.debug("Released EntityManagerFactory");
		}
		catch(final Exception e)
		{
			LOG.error("Failed to release EntityManagerFactory", e);
		}
	}
	
	public static EntityManagerController createForStandalone(
		final String driverFullClassName,
		final String connectionProviderClassName,
		final String jdbcUrl,
		final String username,
		final String password
	)
	{
		return createForStandalone(
			driverFullClassName,
			connectionProviderClassName,
			"Test",
			jdbcUrl,
			username,
			password);
	}
	
	public static EntityManagerController createForStandalone(
		final String driverFullClassName,
		final String connectionProviderClassName,
		final String persistenceUnitName,
		final String jdbcUrl,
		final String username,
		final String password
	)
	{
		final MutablePersistenceUnitInfo persistenceUnitInfo = new MutablePersistenceUnitInfo()
		{
			@Override
			public void addTransformer(final ClassTransformer classTransformer)
			{
				// Do nothing
			}
			
			@Override
			public ClassLoader getNewTempClassLoader()
			{
				return null;
			}
		};
		persistenceUnitInfo.setTransactionType(PersistenceUnitTransactionType.RESOURCE_LOCAL);
		persistenceUnitInfo.setPersistenceUnitName(persistenceUnitName);
		persistenceUnitInfo.setPersistenceProviderClassName(HibernatePersistenceProvider.class.getName());
		AnnotatedClassFinder.find(DefaultJPAConfig.ENTITY_PACKAGE, Entity.class)
			.stream()
			.map(Class::getName)
			.forEach(persistenceUnitInfo::addManagedClassName);
		try
		{
			Collections.list(EntityManagerController.class
					.getClassLoader()
					.getResources(""))
				.forEach(persistenceUnitInfo::addJarFileUrl);
		}
		catch(final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
		
		final Map<String, Object> properties = new HashMap<>(Map.ofEntries(
			entry(JdbcSettings.JAKARTA_JDBC_DRIVER, driverFullClassName),
			entry(JdbcSettings.JAKARTA_JDBC_URL, jdbcUrl),
			entry(JdbcSettings.JAKARTA_JDBC_USER, username),
			entry(JdbcSettings.JAKARTA_JDBC_PASSWORD, password)
		));
		Optional.ofNullable(connectionProviderClassName)
			.ifPresent(p -> properties.put(JdbcSettings.CONNECTION_PROVIDER, connectionProviderClassName));
		properties.putAll(DisableHibernateFormatMapper.properties());
		return new EntityManagerController(
			new HibernatePersistenceProvider()
				.createContainerEntityManagerFactory(
					persistenceUnitInfo,
					properties));
	}
}
