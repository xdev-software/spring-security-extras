package software.xdev.sse.demo.vaadin.cases.urlmapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.List;

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
	void check() throws Exception
	{
		try(final HttpClient client = this.createDefaultHttpClient())
		{
			Assertions.assertAll(this.checkResponse(client.send(
				this.createDefaultHttpRequestBuilder("GET", "/2025/actuator").build(),
				HttpResponse.BodyHandlers.discarding())));
		}
	}
	
	// As of Spring Boot 7.x the underlying problem is fixed out-of-the-box and both responses should now be identical
	protected Collection<Executable> checkResponse(final HttpResponse<?> response)
	{
		return List.of(
			() -> assertEquals(302, response.statusCode()),
			() -> assertTrue(response.headers().firstValue("Set-Cookie").isEmpty()),
			() -> assertTrue(response.headers().firstValue("Location")
				.orElse("")
				.endsWith("/oauth2/authorization/local")),
			() -> assertEquals("1", response.headers().firstValue("X-Force-Reload").orElse(""))
		);
	}
}
