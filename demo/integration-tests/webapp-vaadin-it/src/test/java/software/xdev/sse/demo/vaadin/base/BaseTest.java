package software.xdev.sse.demo.vaadin.base;

import software.xdev.sse.demo.tci.db.DBTCI;
import software.xdev.sse.demo.tci.oidc.OIDCTCI;
import software.xdev.sse.demo.tci.webapp.VaadinWebAppTCI;
import software.xdev.sse.demo.tci.webapp.factory.VaadinWebAppTCIFactory;
import software.xdev.sse.demo.webapp.base.AbstractBaseTest;


@SuppressWarnings("java:S1117")
abstract class BaseTest extends AbstractBaseTest<VaadinWebAppTCI>
{
	protected static final VaadinWebAppTCIFactory APP_INFRA_FACTORY =
		new VaadinWebAppTCIFactory(c -> c.withDB(
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
