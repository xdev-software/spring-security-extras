package software.xdev.sse.demo.tci.db.factory;

import software.xdev.sse.demo.tci.db.DBTCI;
import software.xdev.sse.demo.tci.db.containers.DBContainer;
import software.xdev.sse.demo.tci.db.containers.DBContainerBuilder;
import software.xdev.tci.db.factory.BaseDBTCIFactory;
import software.xdev.tci.factory.prestart.snapshoting.CommitedImageSnapshotManager;
import software.xdev.tci.misc.ContainerMemory;


public class DBTCIFactory extends BaseDBTCIFactory<DBContainer, DBTCI>
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
			() -> new DBContainer(DBContainerBuilder.getBuiltImageName())
				.withDatabaseName(DBTCI.DB_DATABASE)
				.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(ContainerMemory.M512M)),
			"db-mariadb",
			"container.db",
			"DB");
		this.withSnapshotManager(new CommitedImageSnapshotManager("/var/lib/mysql"));
	}
	
	@Override
	protected void warmUpInternal()
	{
		DBContainerBuilder.getBuiltImageName();
		super.warmUpInternal();
	}
}
