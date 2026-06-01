package software.xdev.sse.demo.persistence.config.dialect;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.MariaDBDialect;


/**
 * Spring currently (as of 2024-04) provides no method to specify Dialect + Version, so we have to write our own.
 */
public class DefaultDialect extends MariaDBDialect
{
	@SuppressWarnings("checkstyle:MagicNumber")
	public DefaultDialect()
	{
		super(DatabaseVersion.make(10, 11));
	}
}
