package software.xdev.sse.demo.persistence;

public final class FlywayInfo
{
	private FlywayInfo()
	{
	}
	
	public static final String FLYWAY_TABLENAME = "flywayhistory";
	
	public static final String FLYWAY_LOOKUP_STRUCTURE = "flyway/structure";
	
	public static final String FLYWAY_MIGRATION_PREFIX = "V";
	public static final String FLYWAY_MIGRATION_SEPARATOR = "_";
}
