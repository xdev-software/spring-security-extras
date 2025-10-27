package software.xdev.sse.demo.rest.base;

import software.xdev.sse.demo.tci.db.DBTCI;
import software.xdev.sse.demo.tci.webapp.RestWebAppTCI;
import software.xdev.sse.demo.tci.webapp.factory.RestWebAppTCIFactory;
import software.xdev.sse.demo.webapp.base.AbstractBaseTest;
import software.xdev.tci.oidc.OIDCTCI;


@SuppressWarnings("java:S1117")
abstract class BaseTest extends AbstractBaseTest<RestWebAppTCI>
{
	protected static final RestWebAppTCIFactory APP_INFRA_FACTORY =
		new RestWebAppTCIFactory(c -> c.withDB(
				DBTCI.getInternalJDBCUrl(DNS_NAME_DB),
				DBTCI.DB_USERNAME,
				DBTCI.DB_PASSWORD
			)
			.withAuth(
				OIDCTCI.CLIENT_ID,
				OIDCTCI.CLIENT_SECRET,
				OIDCTCI.getInternalHttpBaseEndPoint(DNS_NAME_OIDC)
			));
	
	protected BaseTest()
	{
		super(APP_INFRA_FACTORY);
	}
}
