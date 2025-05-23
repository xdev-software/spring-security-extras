package software.xdev.sse.oauth2.loginurl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import software.xdev.sse.web.loginurl.DefaultLoginUrlStore;


class OAuth2LoginUrlStoreAdapterTest
{
	static Stream<Arguments> check()
	{
		return Stream.of(
			Arguments.of(DefaultLoginUrlStore.DEFAULT_LOGIN, List.of()),
			Arguments.of(
				OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI + "/a",
				List.of("a")),
			Arguments.of(DefaultLoginUrlStore.DEFAULT_LOGIN, List.of("a", "b"))
		);
	}
	
	@ParameterizedTest(name = "Expecting url={0} for clients={1}")
	@MethodSource
	void check(final String expectedUrl, final List<String> ids)
	{
		assertEquals(expectedUrl, this.getLoginUrlFor(ids));
	}
	
	private String getLoginUrlFor(final List<String> ids)
	{
		final DefaultLoginUrlStore store = new DefaultLoginUrlStore();
		
		final OAuth2LoginConfigurer<HttpSecurity> configurer = new OAuth2LoginConfigurer<>();
		configurer.setBuilder(new HttpSecurity(
			ObjectPostProcessor.identity(),
			new AuthenticationManagerBuilder(ObjectPostProcessor.identity()),
			Map.of()));
		configurer.clientRegistrationRepository(!ids.isEmpty()
			? new InMemoryClientRegistrationRepository(
			ids.stream()
				.map(id -> ClientRegistration.withRegistrationId(id)
					.clientId(id)
					.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
					.redirectUri("http://localhost/" + id)
					.authorizationUri("http://localhost/connect/authorize")
					.tokenUri("http://localhost/connect/token")
					.build())
				.toList())
			: registrationId -> null);
		
		final OAuth2LoginUrlStoreAdapter oAuth2LoginUrlStoreAdapter = new OAuth2LoginUrlStoreAdapter(store);
		Assertions.assertDoesNotThrow(() -> oAuth2LoginUrlStoreAdapter.extractLoginUrls(configurer));
		oAuth2LoginUrlStoreAdapter.postProcess(configurer);
		
		return store.getLoginUrl();
	}
}
