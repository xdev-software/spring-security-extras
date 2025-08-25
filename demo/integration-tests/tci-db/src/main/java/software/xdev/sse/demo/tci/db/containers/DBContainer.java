package software.xdev.sse.demo.tci.db.containers;

import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

import software.xdev.tci.db.containers.WaitableJDBCContainer;


public class DBContainer
	extends MariaDBContainer<DBContainer>
	implements WaitableJDBCContainer
{
	public static final int PORT = 3306;
	
	public DBContainer(final String imageName)
	{
		super(DockerImageName.parse(imageName));
		
		// (31.03.2022 AB)NOTE: https://github.com/testcontainers/testcontainers-java/issues/914
		// Do not mount the volume!
		
		// DO NOT resolve client hostnames for more performance
		// https://mariadb.com/docs/server/ref/mdb/system-variables/skip_name_resolve/
		this.setCommand("--skip-name-resolve");
		
		this.waitingFor(this.completeJDBCWaitStrategy());
	}
	
	@Override
	public WaitStrategy getWaitStrategy()
	{
		return super.getWaitStrategy();
	}
	
	@Override
	public void waitUntilContainerStarted()
	{
		WaitableJDBCContainer.super.waitUntilContainerStarted();
	}
}
