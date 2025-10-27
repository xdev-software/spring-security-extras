package software.xdev.sse.demo.vaadin.base;

import java.time.Duration;
import java.util.function.Consumer;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;

import software.xdev.sse.demo.tci.db.DBTCI;
import software.xdev.sse.demo.tci.webapp.VaadinWebAppTCI;
import software.xdev.sse.demo.tci.webapp.containers.VaadinWebAppContainer;
import software.xdev.sse.demo.tci.webapp.factory.VaadinWebAppPreStartableTCIFactory;
import software.xdev.sse.demo.webapp.base.AbstractBaseTest;
import software.xdev.tci.oidc.OIDCTCI;


@SuppressWarnings("java:S1117")
abstract class BaseTest extends AbstractBaseTest<VaadinWebAppTCI>
{
	protected static final Consumer<VaadinWebAppContainer>
		APP_CONTAINER_BUILDER = c -> c.withDB(
			DBTCI.getInternalJDBCUrl(DNS_NAME_DB),
			DBTCI.DB_USERNAME,
			DBTCI.DB_PASSWORD
		)
		.withAuth(
			OIDCTCI.CLIENT_ID,
			OIDCTCI.CLIENT_SECRET,
			OIDCTCI.getInternalHttpBaseEndPoint(DNS_NAME_OIDC)
		);
	protected static final VaadinWebAppPreStartableTCIFactory APP_INFRA_FACTORY =
		new VaadinWebAppPreStartableTCIFactory(APP_CONTAINER_BUILDER);
	
	protected BaseTest()
	{
		super(APP_INFRA_FACTORY);
	}
	
	protected static CloseableHttpClient createDefaultHttpClient()
	{
		final Duration timeout = Duration.ofSeconds(30);
		return HttpClientBuilder.create()
			.setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
				.setDefaultConnectionConfig(ConnectionConfig.custom()
					.setConnectTimeout(Timeout.of(timeout))
					.setSocketTimeout(Timeout.of(timeout))
					.build())
				.build())
			.disableRedirectHandling()
			.build();
	}
}
