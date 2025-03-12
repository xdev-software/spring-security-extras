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

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTParser;

import software.xdev.sse.oauth2.util.OAuth2UserNameAttributeKeyExtractor;


/**
 * Serializes required auth in a compact way.
 * <p>
 * Please note that ROLES are not serialized due to security concerns.
 * </p>
 */
@SuppressWarnings("java:S4544") // Handled by PolymorphicTypeValidator (see below)
public class DefaultOAuth2CookieRememberMeAuthSerializer implements OAuth2CookieRememberMeAuthSerializer
{
	protected final ObjectMapper mapper;
	
	public DefaultOAuth2CookieRememberMeAuthSerializer()
	{
		this(true);
	}
	
	// Only here for tests
	protected DefaultOAuth2CookieRememberMeAuthSerializer(final boolean withPolymorphicTypeValidator)
	{
		this(withPolymorphicTypeValidator
			// https://cowtowncoder.medium.com/jackson-2-10-safe-default-typing-2d018f0ce2ba
			? BasicPolymorphicTypeValidator.builder()
			.allowIfSubType(Instant.class)
			.allowIfSubType(java.net.URL.class)
			.allowIfSubType(ArrayList.class)
			.build()
			: null);
	}
	
	protected DefaultOAuth2CookieRememberMeAuthSerializer(final PolymorphicTypeValidator polymorphicTypeValidator)
	{
		this.mapper = new ObjectMapper()
			.registerModule(new JavaTimeModule())
			.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		Optional.ofNullable(polymorphicTypeValidator)
			.ifPresent(this.mapper::setPolymorphicTypeValidator);
	}
	
	@Override
	public String serialize(final OAuth2AuthenticationToken token, final OAuth2AuthorizedClient client)
	{
		try
		{
			return this.mapper.writeValueAsString(new SOAuth2AuthContainer(token, client));
		}
		catch(final JsonProcessingException e)
		{
			throw new IllegalStateException("Unable to serialize", e);
		}
	}
	
	@Override
	public OAuth2AuthContainer deserialize(
		final String json,
		final Function<String, ClientRegistration> clientRegistrationResolver)
	{
		try
		{
			return this.mapper.readValue(json, SOAuth2AuthContainer.class)
				.toOriginal(clientRegistrationResolver);
		}
		catch(final JsonProcessingException e)
		{
			throw new IllegalStateException("Unable to deserialize", e);
		}
	}
	
	public record DefaultOAuth2AuthContainer(
		OAuth2AuthenticationToken token,
		OAuth2AuthorizedClient client)
		implements OAuth2AuthContainer
	{
	}
	
	
	public record SOAuth2AuthContainer(
		@JsonProperty("i")
		String clientRegistrationId,
		@JsonProperty("t")
		SOauth2AuthenticationToken token,
		@JsonProperty("c")
		SOAuth2AuthorizedClient client
	)
	{
		public SOAuth2AuthContainer(
			final OAuth2AuthenticationToken token,
			final OAuth2AuthorizedClient client)
		{
			this(
				assertSameClientRegistrationIdAndGet(token, client),
				new SOauth2AuthenticationToken(token),
				new SOAuth2AuthorizedClient(client));
		}
		
		private static String assertSameClientRegistrationIdAndGet(
			final OAuth2AuthenticationToken token,
			final OAuth2AuthorizedClient client)
		{
			final String authorizedClientRegistrationId = token.getAuthorizedClientRegistrationId();
			final String clientRegistrationId = client.getClientRegistration().getRegistrationId();
			if(authorizedClientRegistrationId == null || !Objects.equals(
				authorizedClientRegistrationId,
				clientRegistrationId))
			{
				throw new IllegalArgumentException(
					"clientRegistrationId[authorizedClientRegistrationId='" + authorizedClientRegistrationId
						+ "', clientRegistrationId='"
						+ clientRegistrationId
						+ "'] didn't match");
			}
			return clientRegistrationId;
		}
		
		public DefaultOAuth2AuthContainer toOriginal(final Function<String, ClientRegistration> clientRegistrationResolver)
		{
			final ClientRegistration clientRegistration =
				clientRegistrationResolver.apply(this.clientRegistrationId());
			return new DefaultOAuth2AuthContainer(
				this.token().toOriginal(clientRegistration),
				this.client().toOriginal(clientRegistration));
		}
	}
	
	
	public record SOAuth2AuthorizedClient(
		@JsonProperty("p")
		String principalName,
		@JsonProperty("a")
		SOAuth2AccessToken accessToken,
		@JsonProperty("r")
		SOAuth2RefreshToken refreshToken
	)
	{
		public SOAuth2AuthorizedClient(final OAuth2AuthorizedClient client)
		{
			this(
				client.getPrincipalName(),
				new SOAuth2AccessToken(client.getAccessToken()),
				client.getRefreshToken() != null ? new SOAuth2RefreshToken(client.getRefreshToken()) : null);
		}
		
		public OAuth2AuthorizedClient toOriginal(final ClientRegistration clientRegistration)
		{
			return new OAuth2AuthorizedClient(
				clientRegistration,
				this.principalName(),
				this.accessToken().toOriginal(),
				this.refreshToken() != null ? this.refreshToken().toOriginal() : null);
		}
	}
	
	
	public record SOAuth2AccessToken(
		@JsonProperty("v")
		String tokenValue,
		@JsonProperty("i")
		Instant issuedAt,
		@JsonProperty("e")
		Instant expiresAt,
		@JsonProperty("s")
		Set<String> scopes
	)
	{
		public SOAuth2AccessToken(final OAuth2AccessToken oAuth2AccessToken)
		{
			this(
				oAuth2AccessToken.getTokenValue(),
				oAuth2AccessToken.getIssuedAt(),
				oAuth2AccessToken.getExpiresAt(),
				oAuth2AccessToken.getScopes());
		}
		
		public OAuth2AccessToken toOriginal()
		{
			return new OAuth2AccessToken(
				OAuth2AccessToken.TokenType.BEARER,
				this.tokenValue(),
				this.issuedAt(),
				this.expiresAt(),
				this.scopes());
		}
	}
	
	
	public record SOAuth2RefreshToken(
		@JsonProperty("v")
		String tokenValue,
		@JsonProperty("i")
		Instant issuedAt,
		@JsonProperty("e")
		Instant expiresAt
	)
	{
		public SOAuth2RefreshToken(final OAuth2RefreshToken oAuth2RefreshToken)
		{
			this(
				oAuth2RefreshToken.getTokenValue(),
				oAuth2RefreshToken.getIssuedAt(),
				oAuth2RefreshToken.getExpiresAt());
		}
		
		public OAuth2RefreshToken toOriginal()
		{
			return new OAuth2RefreshToken(
				this.tokenValue(),
				this.issuedAt(),
				this.expiresAt());
		}
	}
	
	
	public record SOauth2AuthenticationToken(
		@JsonProperty("a")
		SOidcUserAuthority oidcUserAuthority,
		@JsonProperty("k")
		String oidcUserNameAttributeKey,
		@JsonProperty("s")
		List<String> simpleGrantedAuthorities,
		@JsonProperty("d")
		SWebAuthenticationDetails details
	)
	{
		public SOauth2AuthenticationToken(final OAuth2AuthenticationToken token)
		{
			this(
				token.getAuthorities()
					.stream()
					.filter(OidcUserAuthority.class::isInstance)
					.map(OidcUserAuthority.class::cast)
					.findFirst()
					.map(SOidcUserAuthority::new)
					.orElse(null),
				OAuth2UserNameAttributeKeyExtractor.extract(token.getPrincipal()),
				token.getAuthorities()
					.stream()
					.filter(SimpleGrantedAuthority.class::isInstance)
					.map(SimpleGrantedAuthority.class::cast)
					.map(SimpleGrantedAuthority::getAuthority)
					// Filter out all roles!
					// 1. These are security critical and need to be re-determined on server restart/hand over.
					// Otherwise, this might result in users having incorrect roles
					// 2. They can also need a lot of storage
					.filter(s -> !s.startsWith("ROLE_"))
					.toList(),
				token.getDetails() instanceof final WebAuthenticationDetails webAuthenticationDetails
					? new SWebAuthenticationDetails(webAuthenticationDetails)
					: null
			);
		}
		
		public OAuth2AuthenticationToken toOriginal(final ClientRegistration clientRegistration)
		{
			final OidcUserAuthority oidcUserAuthority = this.oidcUserAuthority().toOriginal();
			final List<GrantedAuthority> authorities = Stream.concat(
					Stream.of(oidcUserAuthority),
					this.simpleGrantedAuthorities().stream()
						.map(SimpleGrantedAuthority::new))
				.filter(Objects::nonNull)
				.toList();
			final OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(
				new DefaultOidcUser(
					authorities,
					oidcUserAuthority.getIdToken(),
					oidcUserAuthority.getUserInfo(),
					this.oidcUserNameAttributeKey()),
				authorities,
				clientRegistration.getRegistrationId());
			token.setDetails(this.details().toOriginal());
			return token;
		}
	}
	
	
	public record SOidcUserAuthority(
		@JsonProperty("i")
		SOidcIdToken idToken,
		@JsonProperty("u")
		SOidcUserInfo userInfo
	)
	{
		public SOidcUserAuthority(final OidcUserAuthority oidcUserAuthority)
		{
			this(
				new SOidcIdToken(oidcUserAuthority.getIdToken()),
				oidcUserAuthority.getUserInfo() != null
					? SOidcUserInfo.create(oidcUserAuthority.getUserInfo(), oidcUserAuthority.getIdToken())
					: null
			);
		}
		
		public OidcUserAuthority toOriginal()
		{
			final OidcIdToken origIdToken = this.idToken().toOriginal();
			return new OidcUserAuthority(
				origIdToken,
				this.userInfo() != null ? this.userInfo().toOriginal(origIdToken) : null);
		}
	}
	
	
	public record SOidcIdToken(
		@JsonProperty("v")
		String tokenValue,
		@JsonProperty("i")
		Instant issuedAt,
		@JsonProperty("e")
		Instant expiresAt,
		@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
		@JsonProperty("c")
		Map<String, Object> overrideClaims
	)
	{
		public SOidcIdToken(final OidcIdToken token)
		{
			this(
				token.getTokenValue(),
				token.getIssuedAt(),
				token.getExpiresAt(),
				new LinkedHashMap<>(buildOverrideClaims(token))
			);
		}
		
		static Map<String, Object> buildOverrideClaims(final OidcIdToken token)
		{
			final Map<String, Object> parsedClaims = parseClaimsFromToken(token.getTokenValue(), token::getClaims);
			
			final Map<String, Supplier<Instant>> alreadyPresentClaims = Map.of(
				JWTClaimNames.ISSUED_AT, token::getIssuedAt,
				JWTClaimNames.EXPIRATION_TIME, token::getExpiresAt);
			
			// Find out which claims can be restored from the token so that we don't persist the same data multiple
			// times
			return token.getClaims().entrySet()
				.stream()
				.filter(e -> !Optional.ofNullable(parsedClaims.get(e.getKey()))
					.map(parsedValue -> Objects.equals(parsedValue, e.getValue()))
					.orElse(false)
				)
				.filter(e -> !Optional.ofNullable(alreadyPresentClaims.get(e.getKey()))
					.map(alreadyPresentClaim -> Objects.equals(alreadyPresentClaim.get(), e.getValue()))
					.orElse(false))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
		
		static Map<String, Object> parseClaimsFromToken(
			final String tokenValue,
			final Supplier<Map<String, Object>> onError)
		{
			try
			{
				return JWTParser.parse(tokenValue).getJWTClaimsSet().getClaims();
			}
			catch(final ParseException e)
			{
				return onError.get();
			}
		}
		
		public OidcIdToken toOriginal()
		{
			final Map<String, Object> claims = new LinkedHashMap<>(
				parseClaimsFromToken(this.tokenValue(), HashMap::new));
			
			Map.<String, Supplier<Instant>>of(
					JWTClaimNames.ISSUED_AT, this::issuedAt,
					JWTClaimNames.EXPIRATION_TIME, this::expiresAt)
				.forEach((key, valueSupplier) -> Optional.ofNullable(valueSupplier.get())
					.ifPresent(v -> claims.put(key, v)));
			
			// Override
			claims.putAll(this.overrideClaims());
			return new OidcIdToken(this.tokenValue(), this.issuedAt(), this.expiresAt(), claims);
		}
	}
	
	
	public record SOidcUserInfo(
		@JsonProperty("r")
		Set<String> restoreableClaimsFromIdToken,
		@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
		@JsonProperty("c")
		Map<String, Object> overrideClaims
	)
	{
		public static SOidcUserInfo create(final OidcUserInfo userInfo, final OidcIdToken idToken)
		{
			final Map<String, Object> idTokenClaims = idToken.getClaims();
			final Set<String> restoreableClaimsFromIdToken = userInfo.getClaims().entrySet()
				.stream()
				.filter(e -> Optional.ofNullable(idTokenClaims.get(e.getKey()))
					.map(idTokenClaimValue -> Objects.equals(idTokenClaimValue, e.getValue()))
					.orElse(false))
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
			
			return new SOidcUserInfo(
				restoreableClaimsFromIdToken,
				new LinkedHashMap<>(userInfo.getClaims().entrySet()
					.stream()
					.filter(e -> !restoreableClaimsFromIdToken.contains(e.getKey()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));
		}
		
		public OidcUserInfo toOriginal(final OidcIdToken idToken)
		{
			final Map<String, Object> restoredClaims =
				new LinkedHashMap<>(this.restoreableClaimsFromIdToken().stream()
					.filter(key -> idToken.getClaims().containsKey(key))
					.map(key -> Map.entry(key, idToken.getClaims().get(key)))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
			restoredClaims.putAll(this.overrideClaims());
			return new OidcUserInfo(restoredClaims);
		}
	}
	
	
	public record SWebAuthenticationDetails(
		@JsonProperty("r")
		String remoteAddress,
		@JsonProperty("i")
		String sessionId
	)
	{
		public SWebAuthenticationDetails(final WebAuthenticationDetails webAuthenticationDetails)
		{
			this(webAuthenticationDetails.getRemoteAddress(), webAuthenticationDetails.getSessionId());
		}
		
		public WebAuthenticationDetails toOriginal()
		{
			return new WebAuthenticationDetails(this.remoteAddress(), this.sessionId());
		}
	}
}
