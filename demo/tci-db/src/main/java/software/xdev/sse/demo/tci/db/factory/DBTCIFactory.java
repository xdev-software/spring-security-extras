package software.xdev.sse.demo.tci.db.factory;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import org.rnorth.ducttape.unreliables.Unreliables;

import software.xdev.sse.demo.tci.db.DBTCI;
import software.xdev.sse.demo.tci.db.containers.DBContainer;
import software.xdev.sse.demo.tci.util.ContainerMemory;
import software.xdev.tci.factory.prestart.PreStartableTCIFactory;


public class DBTCIFactory extends PreStartableTCIFactory<DBContainer, DBTCI>
{
	public DBTCIFactory()
	{
		this(true);
	}
	
	@SuppressWarnings("resource")
	public DBTCIFactory(final boolean migrateAndInitializeEMC)
	{
		super(
			(c, n) -> new DBTCI(c, n, migrateAndInitializeEMC),
			() -> new DBContainer()
				.withDatabaseName(DBTCI.DB_DATABASE)
				.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(ContainerMemory.M512M)),
			"db-mariadb",
			"container.db",
			"DB");
	}
	
	@Override
	protected void postProcessNew(final DBTCI infra)
	{
		// Docker needs a few milliseconds (usually less than 100) to reconfigure its networks
		// In the meantime existing connections might fail if we go on immediately
		// So let's wait a moment here until everything is fine
		Unreliables.retryUntilSuccess(
			10,
			TimeUnit.SECONDS,
			() -> {
				try(final Connection con = infra.createDataSource().getConnection();
					final Statement statement = con.createStatement())
				{
					statement.executeQuery("SELECT 1").getMetaData();
				}
				
				if(infra.isMigrateAndInitializeEMC())
				{
					// Check EMC if pool connections work
					infra.useNewEntityManager(em -> em.createNativeQuery("SELECT 1").getResultList());
				}
				return null;
			});
	}
}
