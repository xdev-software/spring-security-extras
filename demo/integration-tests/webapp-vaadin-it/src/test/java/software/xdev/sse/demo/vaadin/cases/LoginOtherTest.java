package software.xdev.sse.demo.vaadin.cases;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpHead;
import org.apache.hc.client5.http.classic.methods.HttpOptions;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpTrace;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.utils.Base64;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.util.Timeout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import software.xdev.sse.demo.vaadin.base.InfraPerClassTest;


class LoginOtherTest extends InfraPerClassTest
{
	@DisplayName("No session should be created for public static resource")
	@ParameterizedTest(name = "{displayName} [method={2}]")
	@MethodSource
	void checkNoSessionCreatedForPublicStaticResource(final String method) throws IOException
	{
		try(final CloseableHttpClient client = createDefaultHttpClient())
		{
			final HttpUriRequestBase http = new HttpUriRequestBase(
				method,
				URI.create(this.appInfra().getExternalHTTPEndpoint() + "/robots.txt"));
			try(final ClassicHttpResponse response = client.execute(http, r -> r))
			{
				assertAll(this.assertsNoSessionNoLoginAndCode(HttpStatus.SC_OK, response));
			}
		}
	}
	
	static Stream<Arguments> checkNoSessionCreatedForPublicStaticResource()
	{
		return ALL_SUPPORTED_HTTP_METHODS.stream()
			.map(Arguments::of);
	}
	
	@DisplayName("No session should be created for actuator")
	@ParameterizedTest(name = "{displayName} [withAuth={0}, existingPath={1}, method={2}] expect={3}")
	@MethodSource
	void checkNoSessionCreatedForActuator(
		final boolean withAuth,
		final boolean existingPath,
		final String method,
		final int expectedCode)
		throws IOException
	{
		try(final CloseableHttpClient client = createDefaultHttpClient())
		{
			final HttpUriRequestBase http = new HttpUriRequestBase(
				method,
				URI.create(this.appInfra().getExternalHTTPEndpoint() + "/actuator" + (existingPath ? "" : "/abc")));
			if(withAuth)
			{
				final String auth =
					this.appInfra().getActuatorUsername() + ":" + this.appInfra().getActuatorPassword();
				http.setHeader(
					HttpHeaders.AUTHORIZATION,
					"Basic " + new String(Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1))));
			}
			try(final ClassicHttpResponse response = client.execute(http, r -> r))
			{
				assertAll(this.assertsNoSessionNoLoginAndCode(expectedCode, response));
			}
		}
	}
	
	static Stream<Arguments> checkNoSessionCreatedForActuator()
	{
		final Set<String> allowedAndExistingOkMethods = Set.of(
			HttpGet.METHOD_NAME,
			HttpOptions.METHOD_NAME,
			HttpHead.METHOD_NAME);
		
		final Set<String> allowedAndNotExistingOkMethods = Set.of(
			HttpOptions.METHOD_NAME
		);
		
		return Stream.of(
			// NO AUTH but ENDPOINT EXISTS
			ALL_SUPPORTED_HTTP_METHODS.stream()
				.map(method -> Arguments.of(false, true, method, HttpStatus.SC_UNAUTHORIZED)),
			// AUTH and ENDPOINT EXISTS
			allowedAndExistingOkMethods.stream()
				.map(method -> Arguments.of(true, true, method, HttpStatus.SC_OK)),
			ALL_SUPPORTED_HTTP_METHODS.stream()
				.filter(m -> !allowedAndExistingOkMethods.contains(m))
				.map(method -> Arguments.of(true, true, method, HttpStatus.SC_METHOD_NOT_ALLOWED)),
			// AUTH and INVALID ENDPOINT
			allowedAndNotExistingOkMethods.stream()
				.map(method -> Arguments.of(true, false, method, HttpStatus.SC_OK)),
			ALL_SUPPORTED_HTTP_METHODS.stream()
				.filter(m -> !allowedAndNotExistingOkMethods.contains(m))
				.map(method -> Arguments.of(true, false, method, HttpStatus.SC_NOT_FOUND)),
			// NO AUTH and INVALID ENDPOINT
			ALL_SUPPORTED_HTTP_METHODS.stream()
				.map(method -> Arguments.of(false, false, method, HttpStatus.SC_UNAUTHORIZED)),
			// TRACE is not supported by Spring Boot
			Stream.of(Arguments.of(false, false, HttpTrace.METHOD_NAME, HttpStatus.SC_METHOD_NOT_ALLOWED))
		).flatMap(Function.identity());
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
	
	private Stream<Executable> assertsNoSessionNoLoginAndCode(final int expectedCode, final HttpResponse response)
	{
		return Stream.of(
			() -> assertEquals(expectedCode, response.getCode()),
			() -> assertNull(response.getHeader("Set-Cookie")),
			() -> assertFalse(() -> this.appInfra()
				.getContainer()
				.getLogs()
				.contains("SEC_LEAK"))
		);
	}
	
	static final List<String> ALL_SUPPORTED_HTTP_METHODS = List.of(
		HttpGet.METHOD_NAME,
		HttpPost.METHOD_NAME,
		HttpPut.METHOD_NAME,
		HttpDelete.METHOD_NAME,
		HttpHead.METHOD_NAME,
		HttpOptions.METHOD_NAME);
}
