package software.xdev.sse.demo.vaadin.cases.urlmapping;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.testcontainers.containers.Network;

import software.xdev.sse.demo.tci.webapp.VaadinWebAppTCI;
import software.xdev.sse.demo.tci.webapp.containers.VaadinWebAppContainer;
import software.xdev.sse.demo.tci.webapp.factory.VaadinWebAppOnDemandTCIFactory;
import software.xdev.sse.demo.vaadin.base.InfraPerClassTest;


abstract class BaseUrlMappingTest extends InfraPerClassTest
{
	protected static final VaadinWebAppOnDemandTCIFactory APP_ON_DEMAND_TCI_FACTORY =
		new VaadinWebAppOnDemandTCIFactory(
			"url-mapping", APP_CONTAINER_BUILDER);
	
	@Override
	protected VaadinWebAppTCI createAppInfra(final Network network, final String dnsName)
	{
		return APP_ON_DEMAND_TCI_FACTORY.getNew(
			network,
			this::customizeWebAppContainer);
	}
	
	protected void customizeWebAppContainer(final VaadinWebAppContainer c)
	{
		c.withEnv("VAADIN_URL-MAPPING", "/2025/*");
	}
	
	@Test
	void check() throws IOException
	{
		try(final CloseableHttpClient client = createDefaultHttpClient())
		{
			final HttpUriRequestBase http = new HttpUriRequestBase(
				"GET",
				URI.create(this.appInfra().getExternalHTTPEndpoint() + "/2025/actuator"));
			
			try(final ClassicHttpResponse response = client.execute(http, r -> r))
			{
				Assertions.assertAll(this.checkResponse(response));
			}
		}
	}
	
	protected abstract Collection<Executable> checkResponse(final ClassicHttpResponse response);
}
