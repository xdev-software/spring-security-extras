package software.xdev.sse.demo.vaadin.base;

import java.util.Objects;
import java.util.function.Consumer;

import org.openqa.selenium.JavascriptExecutor;

import software.xdev.sse.demo.tci.db.DBTCI;
import software.xdev.sse.demo.tci.webapp.VaadinWebAppTCI;
import software.xdev.sse.demo.tci.webapp.containers.VaadinWebAppContainer;
import software.xdev.sse.demo.tci.webapp.factory.VaadinWebAppPreStartableTCIFactory;
import software.xdev.sse.demo.webapp.base.AbstractBaseTest;
import software.xdev.tci.oidc.OIDCTCI;


@SuppressWarnings("java:S1117")
abstract class BaseTest extends AbstractBaseTest<VaadinWebAppTCI>
{
	protected static final Consumer<VaadinWebAppContainer> APP_CONTAINER_BUILDER =
		c -> c
			.withDebugRootLogger()
			.withDB(
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
	
	@Override
	public void navigateTo(final String... additionalPathSegments)
	{
		this.navigateToWithoutWait(additionalPathSegments);
		this.waitForDocumentAndVaadinReady();
	}
	
	@Override
	public void checkForMainPage()
	{
		this.waitForDocumentAndVaadinReady();
		super.checkForMainPage();
	}
	
	void waitForDocumentAndVaadinReady()
	{
		this.waitUntil(
			d -> Objects.equals(
				((JavascriptExecutor)d).executeScript("if (document.readyState != 'complete') {"
					+ "  return false;"
					+ "}"
					+ "if (window.Vaadin?.Flow?.clients) {"
					+ "  var clients = window.Vaadin.Flow.clients;"
					+ "  for (var client in clients) {"
					+ "    if (clients[client].isActive()) {"
					+ "      return false;"
					+ "    }"
					+ "  }"
					+ "}"
					+ "return true;"),
				Boolean.TRUE));
	}
}
