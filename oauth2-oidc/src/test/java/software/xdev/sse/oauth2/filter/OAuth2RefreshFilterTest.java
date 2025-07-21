package software.xdev.sse.oauth2.filter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.DummyHttpServletRequest;
import jakarta.servlet.http.DummyHttpServletResponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import software.xdev.sse.oauth2.checkauth.MockOAuth2ProviderOfflineManager;
import software.xdev.sse.oauth2.checkauth.OAuth2AuthChecker;
import software.xdev.sse.oauth2.checkauth.config.AuthProviderOfflineConfig;
import software.xdev.sse.oauth2.filter.handler.OAuth2RefreshHandler;
import software.xdev.sse.oauth2.filter.metrics.DummyOAuth2RefreshFilterAuthCheckMetrics;
import software.xdev.sse.oauth2.filter.reloadcom.OAuth2RefreshReloadCommunicator;
import software.xdev.sse.oauth2.util.NoOpMockDynamicLazyBeanProvider;


class OAuth2RefreshFilterTest
{
	@Test
	void checkDeAuthDueToExpiredToken()
	{
		final Instant initialNow = LocalDate.of(2000, 1, 1)
			.atStartOfDay()
			.toInstant(ZoneOffset.UTC);
		final AtomicReference<Instant> now = new AtomicReference<>(initialNow);
		
		final String clientId = "test";
		final ClientRegistration clientReg = ClientRegistration.withRegistrationId(clientId)
			.clientId(clientId)
			.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
			.redirectUri("http://localhost/" + clientId)
			.authorizationUri("http://localhost/connect/authorize")
			.tokenUri("http://localhost/connect/token")
			.build();
		
		final InMemoryOAuth2AuthorizedClientService clientService =
			new InMemoryOAuth2AuthorizedClientService(regId -> clientId.equals(regId) ? clientReg : null);
		
		final OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(
			clientReg,
			"client-principalName",
			new OAuth2AccessToken(
				OAuth2AccessToken.TokenType.BEARER,
				"accessToken-value",
				initialNow,
				initialNow.plusSeconds(1))
		);
		final OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(
			new DefaultOidcUser(
				List.of(),
				new OidcIdToken(
					"oidcIdToken-value",
					initialNow,
					initialNow.plusSeconds(1),
					Map.of("sub", "dummy"))),
			List.of(),
			clientId);
		clientService.saveAuthorizedClient(client, token);
		
		now.set(now.get().plusSeconds(10)); // Advance time
		
		final AtomicBoolean deAuth = new AtomicBoolean(false);
		
		final OAuth2RefreshFilter filter = new OAuth2RefreshFilter(
			new DummyOAuth2RefreshFilterAuthCheckMetrics(),
			clientService,
			new OAuth2AuthChecker(
				OAuth2AuthorizeRequest::getAuthorizedClient,
				new MockOAuth2ProviderOfflineManager(new AuthProviderOfflineConfig(), now),
				t -> false
			),
			(request, response, auth) -> {
				deAuth.set(true);
			},
			new NoOpMockDynamicLazyBeanProvider<>(OAuth2RefreshHandler.class),
			new NoOpMockDynamicLazyBeanProvider<>(OAuth2RefreshReloadCommunicator.class))
		{
			@Override
			protected Authentication getCurrentAuth(final ServletRequest request, final ServletResponse response)
			{
				return token;
			}
		};
		
		filter.checkAuth(new DummyHttpServletRequest(), new DummyHttpServletResponse());
		
		Assertions.assertTrue(deAuth.get());
	}
}
