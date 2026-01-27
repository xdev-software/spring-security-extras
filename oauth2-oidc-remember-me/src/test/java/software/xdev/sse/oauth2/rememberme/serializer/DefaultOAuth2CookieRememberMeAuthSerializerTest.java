/*
 * Copyright © 2025 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.sse.oauth2.rememberme.serializer;

import static java.util.Map.entry;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.json.JsonMapper;


// NOTE: Originally designed for Jackson v2
// Was improved in v3 so that for example Object objects are no longer deserialized into
// potentially dangerous classes.
// Please note that Jackson v3 does not cover all possible options (as it depends on the available classes)
// and therefore this test is still present
class DefaultOAuth2CookieRememberMeAuthSerializerTest
{
	private static final String ACCESS_TOKEN = "dummy";
	private static final String REFRESH_TOKEN = "dummy ";
	private static final String ID_TOKEN = "eyJhbGciOiJSUzI1NiIsImtpZCI6IkQxRDYyRTQwNEY5N0M4OUMxRENDM0NGN"
		+ "zE0Njc2NDgyIiwidHlwIjoiSldUIn0.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjQwMTEiLCJuYmYiOjE3MTI1NzYwMzc"
		+ "sImlhdCI6MTcxMjU3NjAzNywiZXhwIjoxNzEyNTc2MzM3LCJhdWQiOiJ0aW1lbGluZS1jbGllbnQtaWQiLCJhbXIiOlsic"
		+ "HdkIl0sIm5vbmNlIjoiaXZQeDk0SkdSN0NCT2pMVUZRZ0pLMFl5THBLeGhOSTZ0eXh2WjVtWXpTbyIsImF0X2hhc2giOiJ"
		+ "JQlZNV1pDM29jcXdGU0dQREJTdWVRIiwic2lkIjoiMEYyQzE1REMxNDFBREE5QTlEMTExMTIxQUZFMDE0OTIiLCJzdWIiO"
		+ "iIxIiwiYXV0aF90aW1lIjoxNzEyNTYwODE4LCJpZHAiOiJsb2NhbCIsIm5hbWUiOiJBbGV4YW5kZXIgQmllcmxlciIsImV"
		+ "tYWlsIjoiYS5iaWVybGVyQHhkZXYtc29mdHdhcmUuZGUifQ.MGU-vvHNoxpcI6sSzTGQgPc1_Lng6C6Cml8Za-aeppwyCd"
		+ "IRPlE-TP41nb3KGabxR31rMImexGDnQu1mCo7YkAmj6qoYffqnUk04m_5YVmaPZdTb0XrvBIroAkIrIqOQrYjaIZS1SjKR"
		+ "3lM3j6y8CwprrJUdMc3DXcTDVMKaV7pbTPYOuwnagjpobCOxZFCWDj5EVNOih-dkOw04DL0tK_vxRZJk84LMIHxhXLucai"
		+ "h7YAn24KZK8tsWxxjNP9Q4yUSPiKpxk2TMDzN-X-HOgh37uUvxhovbMkyKMJCb62nZSRdOm488tg9K9d_Ua0ZVfrip-Khh"
		+ "u3XA6fGFNMWtzA";
	private static final Instant ID_TOKEN_ISSUED_AT = Instant.parse("2024-04-08T11:33:57Z");
	private static final Instant ID_TOKEN_EXPIRE = ID_TOKEN_ISSUED_AT.plus(Duration.ofMinutes(5));
	private static final Instant ACCESS_REFRESH_TOKEN_ISSUED_AT = Instant.parse("2024-04-08T11:33:57.623028700Z");
	private static final String NAME = "A B";
	private static final String EMAIL = "a.b@xdev-software.de";
	
	private static final Consumer<JsonMapper.Builder> SERIALIZER_JSON_MAPPER_CUSTOMIZER =
		b -> b.enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION);
	
	static DefaultOAuth2CookieRememberMeAuthSerializer createSerializer(final boolean safe)
	{
		return new DefaultOAuth2CookieRememberMeAuthSerializer(safe, SERIALIZER_JSON_MAPPER_CUSTOMIZER);
	}
	
	@Test
	void defaultSerializationWorks()
	{
		Assertions.assertDoesNotThrow(() -> this.serializeAndDeserialize(
			createSerializer(true),
			Map.of()));
	}
	
	@SuppressWarnings("checkstyle:VisibilityModifier")
	static Set<String> attackSuccessIds = new HashSet<>();
	
	@Test
	void performAttackWithoutDedicatedProtection()
	{
		final String id = "default";
		final var serializer = createSerializer(false);
		final InvalidDefinitionException ex = Assertions.assertThrows(
			InvalidDefinitionException.class,
			() -> this.performAttack(serializer, id)
		);
		Assertions.assertTrue(ex.getMessage().startsWith("Configured `PolymorphicTypeValidator`")
			&& ex.getMessage().contains("denies resolution of all subtypes of base type "
			+ "`java.lang.Object` as using too generic base type "
			+ "can open a security hole without checks on subtype: "
			+ "please configure a custom `PolymorphicTypeValidator` for this use case"));
		Assertions.assertFalse(attackSuccessIds.contains(id));
	}
	
	@Test
	void performAttackFails()
	{
		final String id = "fail";
		final var serializer = createSerializer(true);
		final InvalidTypeIdException ex = Assertions.assertThrows(
			InvalidTypeIdException.class,
			() -> this.performAttack(serializer, id)
		);
		Assertions.assertTrue(ex.getMessage().startsWith("Could not resolve type id")
			&& ex.getMessage().contains(
			"$AttackPerformer' as a subtype of `java.lang.Object`: Configured `PolymorphicTypeValidator`"));
		Assertions.assertFalse(attackSuccessIds.contains(id));
	}
	
	void performAttack(
		final DefaultOAuth2CookieRememberMeAuthSerializer serializer,
		final String id)
	{
		final Map<String, Object> data = Map.of("test", new AttackPerformer(id));
		this.serializeAndDeserialize(serializer, data);
	}
	
	public static class AttackPerformer
	{
		public static final String SUCCESS_INDICATOR = "ATTACK_PERF_SUCCESS";
		
		private String id;
		
		// Required for Jackson
		public AttackPerformer()
		{
		}
		
		public AttackPerformer(final String id)
		{
			this.id = id;
		}
		
		public String getId()
		{
			return this.id;
		}
		
		public void setId(final String id)
		{
			this.id = id;
			
			// ATTACK
			attackSuccessIds.add(id);
			throw new IllegalStateException(SUCCESS_INDICATOR);
		}
	}
	
	@SuppressWarnings("PMD.ReplaceJavaUtilDate")
	void serializeAndDeserialize(
		final DefaultOAuth2CookieRememberMeAuthSerializer serializer,
		final Map<String, Object> additionalClaims)
	{
		try
		{
			final ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("local")
				.clientId("timeline-client-id")
				.clientSecret("timeline-client-secret")
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}")
				.authorizationUri("http://localhost:4011/connect/authorize")
				.tokenUri("http://localhost:4011/connect/token")
				.jwkSetUri("http://localhost:4011/.well-known/openid-configuration/jwks")
				.issuerUri("http://localhost:4011")
				.scope("openid", "profile", "email")
				.build();
			
			final Map<String, Object> claims = new HashMap<>(Map.ofEntries(
				entry(IdTokenClaimNames.AT_HASH, "IBVMWZC3ocqwFSGPDBSueQ"),
				entry(IdTokenClaimNames.SUB, "1"),
				entry(IdTokenClaimNames.AMR, new ArrayList<>(List.of("pwd"))),
				entry(IdTokenClaimNames.ISS, URI.create("http://localhost:4011").toURL()),
				entry(IdTokenClaimNames.NONCE, "ivPx94JGR7CBOjLUFQgJK0YyLpKxhNI6tyxvZ5mYzSo"),
				entry("sid", "0F2C15DC141ADA9A9D111121AFE01492"),
				entry(IdTokenClaimNames.AUD, new ArrayList<>(List.of("timeline-client-id"))),
				entry("nbf", new Date(1712576037000L)),
				entry("idp", "local"),
				entry(IdTokenClaimNames.AUTH_TIME, Instant.parse("2024-04-08T07:20:18Z")),
				entry("name", NAME),
				entry(IdTokenClaimNames.EXP, ID_TOKEN_EXPIRE),
				entry(IdTokenClaimNames.IAT, ID_TOKEN_ISSUED_AT),
				entry("email", EMAIL)
			));
			
			claims.putAll(additionalClaims);
			final OidcIdToken idToken = new OidcIdToken(
				ID_TOKEN,
				ID_TOKEN_ISSUED_AT,
				ID_TOKEN_EXPIRE,
				claims);
			final OidcUserInfo userInfo = new OidcUserInfo(Map.of(
				IdTokenClaimNames.SUB, "1",
				"name", NAME,
				"email", EMAIL
			));
			final DefaultOidcUser oauth2User = new DefaultOidcUser(
				List.of(
					new OidcUserAuthority(idToken, userInfo),
					new SimpleGrantedAuthority("SCOPE_openid"),
					new SimpleGrantedAuthority("SCOPE_profile"),
					new SimpleGrantedAuthority("SCOPE_email")),
				idToken,
				userInfo
			);
			
			final OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(
				oauth2User,
				oauth2User.getAuthorities(),
				clientRegistration.getRegistrationId());
			token.setDetails(new WebAuthenticationDetails("http://localhost:12345", "12345678"));
			final String serialized = serializer.serialize(
				token,
				new OAuth2AuthorizedClient(
					clientRegistration,
					oauth2User.getName(),
					new OAuth2AccessToken(
						OAuth2AccessToken.TokenType.BEARER,
						ACCESS_TOKEN,
						ACCESS_REFRESH_TOKEN_ISSUED_AT,
						ACCESS_REFRESH_TOKEN_ISSUED_AT.plus(Duration.ofMinutes(1))),
					new OAuth2RefreshToken(
						REFRESH_TOKEN,
						ACCESS_REFRESH_TOKEN_ISSUED_AT)
				)
			);
			serializer.deserialize(
				serialized,
				new InMemoryClientRegistrationRepository(clientRegistration)::findByRegistrationId);
		}
		catch(final MalformedURLException e)
		{
			// Never happens
			throw new RuntimeException(e);
		}
	}
}
