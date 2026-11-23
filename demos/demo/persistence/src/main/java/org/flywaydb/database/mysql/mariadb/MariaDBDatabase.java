package org.flywaydb.database.mysql.mariadb;

import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.internal.jdbc.JdbcConnectionFactory;
import org.flywaydb.core.internal.jdbc.StatementInterceptor;
import org.flywaydb.database.mysql.MySQLDatabase;


/**
 * Fork of default MariaDBDatabase
 */
public class MariaDBDatabase extends MySQLDatabase
{
	public MariaDBDatabase(
		final Configuration configuration,
		final JdbcConnectionFactory jdbcConnectionFactory,
		final StatementInterceptor statementInterceptor)
	{
		super(configuration, jdbcConnectionFactory, statementInterceptor);
	}
	
	@Override
	protected String getConstraintName(final String tableName)
	{
		return "";
	}
	
	@Override
	public void ensureSupported(final Configuration configuration)
	{
		// YES it is supported for what we do - stop spamming the log
	}
}
