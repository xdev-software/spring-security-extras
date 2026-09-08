package software.xdev.sse.demo.rest.cases;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import software.xdev.sse.demo.rest.base.InfraPerClassTest;


class LoginOtherTest extends InfraPerClassTest
{
	static final Set<String> NON_CSRF_METHODS = Set.of(
		"GET",
		"OPTIONS",
		"HEAD");
	
	@DisplayName("No session should be created for public static resource")
	@ParameterizedTest(name = "{displayName} [method={0}] expect={1}")
	@MethodSource
	void checkNoSessionCreatedForPublicStaticResource(final String method, final int expectedCode) throws Exception
	{
		try(final HttpClient client = this.createDefaultHttpClient())
		{
			assertAll(this.assertsNoSessionNoLoginAndCode(
				expectedCode,
				client.send(
					this.createDefaultHttpRequestBuilder(method, "/robots.txt").build(),
					HttpResponse.BodyHandlers.discarding())));
		}
	}
	
	static Stream<Arguments> checkNoSessionCreatedForPublicStaticResource()
	{
		return Stream.concat(
			NON_CSRF_METHODS.stream()
				.map(m -> Arguments.of(m, 200)),
			ALL_SUPPORTED_HTTP_METHODS.stream()
				.filter(m -> !NON_CSRF_METHODS.contains(m))
				.map(m -> Arguments.of(m, 405))
		);
	}
	
	@DisplayName("No session should be created for actuator")
	@ParameterizedTest(name = "{displayName} [withAuth={0}, existingPath={1}, method={2}] expect={3}")
	@MethodSource
	void checkNoSessionCreatedForActuator(
		final boolean withAuth,
		final boolean existingPath,
		final String method,
		final int expectedCode)
		throws Exception
	{
		try(final HttpClient client = this.createDefaultHttpClient())
		{
			final HttpRequest.Builder requestBuilder =
				this.createDefaultHttpRequestBuilder(method, "/actuator" + (existingPath ? "" : "/abc"));
			if(withAuth)
			{
				final String auth =
					this.appInfra().getActuatorUsername() + ":" + this.appInfra().getActuatorPassword();
				requestBuilder.header(
					"Authorization",
					"Basic " + new String(Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1))));
			}
			assertAll(this.assertsNoSessionNoLoginAndCode(
				expectedCode,
				client.send(requestBuilder.build(), HttpResponse.BodyHandlers.discarding())));
		}
	}
	
	static Stream<Arguments> checkNoSessionCreatedForActuator()
	{
		return Stream.of(
			// NO AUTH but ENDPOINT EXISTS
			ALL_SUPPORTED_HTTP_METHODS.stream()
				.map(method -> Arguments.of(false, true, method, 401)),
			// AUTH and ENDPOINT EXISTS
			NON_CSRF_METHODS.stream()
				.map(method -> Arguments.of(true, true, method, 200)),
			ALL_SUPPORTED_HTTP_METHODS.stream()
				.filter(m -> !NON_CSRF_METHODS.contains(m))
				.map(method -> Arguments.of(true, true, method, 405)),
			// AUTH and INVALID ENDPOINT
			ALL_SUPPORTED_HTTP_METHODS.stream()
				.map(method -> Arguments.of(true, false, method, 404)),
			// NO AUTH and INVALID ENDPOINT
			ALL_SUPPORTED_HTTP_METHODS.stream()
				.map(method -> Arguments.of(false, false, method, 401)),
			// TRACE is not supported by Spring Boot
			Stream.of(false, true)
				.map(existingPath ->
					Arguments.of(false, existingPath, "TRACE", 405))
		).flatMap(Function.identity());
	}
	
	private Stream<Executable> assertsNoSessionNoLoginAndCode(final int expectedCode, final HttpResponse<?> response)
	{
		return Stream.of(
			() -> assertEquals(expectedCode, response.statusCode()),
			() -> assertTrue(response.headers().firstValue("Set-Cookie").isEmpty())
		);
	}
	
	static final List<String> ALL_SUPPORTED_HTTP_METHODS = List.of(
		"GET",
		"POST",
		"PUT",
		"DELETE",
		"HEAD",
		"OPTIONS");
}
