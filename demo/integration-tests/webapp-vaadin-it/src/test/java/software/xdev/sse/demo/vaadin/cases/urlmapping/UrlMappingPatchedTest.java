package software.xdev.sse.demo.vaadin.cases.urlmapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.jupiter.api.function.Executable;


class UrlMappingPatchedTest extends BaseUrlMappingTest
{
	@Override
	protected Collection<Executable> checkResponse(final ClassicHttpResponse response)
	{
		return List.of(
			() -> assertEquals(302, response.getCode()),
			() -> assertNull(response.getHeader("Set-Cookie")),
			() -> assertTrue(response.getHeader("Location")
				.getValue()
				.endsWith("/oauth2/authorization/local")),
			() -> assertEquals("1", response.getHeader("X-Force-Reload").getValue())
		);
	}
}
