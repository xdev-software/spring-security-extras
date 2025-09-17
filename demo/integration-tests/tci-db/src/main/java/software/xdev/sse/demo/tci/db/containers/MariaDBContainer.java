package software.xdev.sse.demo.tci.db.containers;

import java.util.concurrent.Future;

import org.testcontainers.containers.ContainerLaunchException;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;


@SuppressWarnings({"java:S119", "java:S2160", "java:S2068"})
public class MariaDBContainer<SELF extends MariaDBContainer<SELF>> extends JdbcDatabaseContainer<SELF>
{
	protected static final String DEFAULT_USER = "test";
	
	protected static final String DEFAULT_PASSWORD = "test";
	
	protected static final Integer MARIADB_PORT = 3306;
	
	protected String databaseName = "test";
	
	protected String username = DEFAULT_USER;
	
	protected String password = DEFAULT_PASSWORD;
	
	protected static final String MARIADB_ROOT_USER = "root";
	
	public MariaDBContainer(final Future<String> image)
	{
		super(image);
	}
	
	public MariaDBContainer(final DockerImageName dockerImageName)
	{
		super(dockerImageName);
		this.addExposedPort(MARIADB_PORT);
	}
	
	// https://github.com/testcontainers/testcontainers-java/issues/10359
	
	@Override
	protected void configure()
	{
		this.addEnv("MARIADB_DATABASE", this.databaseName);
		
		if(!MARIADB_ROOT_USER.equalsIgnoreCase(this.username))
		{
			this.addEnv("MARIADB_USER", this.username);
		}
		if(this.password != null && !this.password.isEmpty())
		{
			this.addEnv("MARIADB_PASSWORD", this.password);
			this.addEnv("MARIADB_ROOT_PASSWORD", this.password);
		}
		else if(MARIADB_ROOT_USER.equalsIgnoreCase(this.username))
		{
			this.addEnv("MARIADB_ALLOW_EMPTY_PASSWORD", "yes");
		}
		else
		{
			throw new ContainerLaunchException("Empty password can be used only with the root user");
		}
	}
	
	@Override
	public String getDriverClassName()
	{
		return "org.mariadb.jdbc.Driver";
	}
	
	@Override
	public String getJdbcUrl()
	{
		return "jdbc:mariadb://"
			+ this.getHost()
			+ ":"
			+ this.getMappedPort(MARIADB_PORT)
			+ "/"
			+ this.databaseName
			+ this.constructUrlParameters("?", "&");
	}
	
	@Override
	public String getDatabaseName()
	{
		return this.databaseName;
	}
	
	@Override
	public String getUsername()
	{
		return this.username;
	}
	
	@Override
	public String getPassword()
	{
		return this.password;
	}
	
	@Override
	public String getTestQueryString()
	{
		return "SELECT 1";
	}
	
	@Override
	public SELF withDatabaseName(final String databaseName)
	{
		this.databaseName = databaseName;
		return this.self();
	}
	
	@Override
	public SELF withUsername(final String username)
	{
		this.username = username;
		return this.self();
	}
	
	@Override
	public SELF withPassword(final String password)
	{
		this.password = password;
		return this.self();
	}
}
