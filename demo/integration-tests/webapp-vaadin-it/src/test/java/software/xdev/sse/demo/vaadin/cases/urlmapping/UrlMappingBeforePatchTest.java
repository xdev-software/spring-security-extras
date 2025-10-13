package software.xdev.sse.demo.vaadin.cases.urlmapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collection;
import java.util.List;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.jupiter.api.function.Executable;

import software.xdev.sse.demo.tci.webapp.containers.VaadinWebAppContainer;


class UrlMappingBeforePatchTest extends BaseUrlMappingTest
{
	@Override
	protected void customizeWebAppContainer(final VaadinWebAppContainer c)
	{
		super.customizeWebAppContainer(c);
		c.withEnv("SSE_SIDECAR_HTTP-SECURITY-MATCHER_DEFAULT_CREATOR_ENABLED", "false");
	}
	
	@Override
	protected Collection<Executable> checkResponse(final ClassicHttpResponse response)
	{
		return List.of(
			() -> assertEquals(401, response.getCode()),
			() -> assertNull(response.getHeader("Set-Cookie")),
			() -> assertEquals("Basic realm=\"Realm\"", response.getHeader("WWW-Authenticate").getValue())
		);
	}
}
