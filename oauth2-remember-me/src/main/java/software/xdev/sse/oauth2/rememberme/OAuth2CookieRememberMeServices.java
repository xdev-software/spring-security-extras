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
package software.xdev.sse.oauth2.rememberme;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.UnaryOperator;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AbstractOAuth2Token;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.ConcurrentReferenceHashMap;

import software.xdev.sse.oauth2.checkauth.OAuth2AuthChecker;
import software.xdev.sse.oauth2.cookie.CookieSecureService;
import software.xdev.sse.oauth2.filter.handler.OAuth2RefreshHandler;
import software.xdev.sse.oauth2.rememberme.clientstorage.RememberMeClientStorageProcessor;
import software.xdev.sse.oauth2.rememberme.clientstorage.RememberMeClientStorageProcessorProvider;
import software.xdev.sse.oauth2.rememberme.config.OAuth2CookieRememberMeServicesConfig;
import software.xdev.sse.oauth2.rememberme.crypt.RememberMeSymCryptManager;
import software.xdev.sse.oauth2.rememberme.crypt.RememberMeSymCryptorProvider;
import software.xdev.sse.oauth2.rememberme.metrics.AutoLoginMetrics;
import software.xdev.sse.oauth2.rememberme.secrets.AuthRememberMeSecret;
import software.xdev.sse.oauth2.rememberme.secrets.AuthRememberMeSecretService;
import software.xdev.sse.oauth2.rememberme.serializer.OAuth2CookieRememberMeAuthSerializer;
import software.xdev.sse.oauth2.util.FastCookieFinder;
import software.xdev.sse.oauth2.util.OAuth2AuthenticationTokenUtil;


/**
 * <p>
 * <b>TL;DR:</b> Remembers the OAuth2 login by persisting it into a Cookie.
 * </p>
 * <p>
 * Addresses the following problem:
 * </p>
 * <p>
 * On session loss (e.g. after server restart or session expiration) all auth data is lost which will require the user
 * to perform a login<br/> The login could be done with
 * {@link software.xdev.sse.oauth2.rememberloginproviderredirect.CookieBasedRememberRedirectOAuth2LoginProvider}
 * automatically on the client side however there are a few problems:
 * <ul>
 *     <li>users get redirected which might be annoying</li>
 *     <li>if the provider is offline no auth can occur, this also can't be addressed with
 *     {@link software.xdev.sse.oauth2.checkauth.OAuth2ProviderOfflineManager}
 *     because it only works after auth
 *     </li>
 *     <li>doesn't allow clustering with multiple instances</li>
 * </ul>
 * </p>
 * <p>
 * Uses the following security system to achieve safe storage of data:
 * <table>
 *     <tr>
 *         <th>Location</th>
 *         <th>Stores</th>
 *     </tr>
 *     <tr>
 *         <td>Client</td>
 *         <td>2 Cookies: ID-Cookie and an encrypted Payload-Cookie with the auth</td>
 *     </tr>
 *     <tr>
 *         <td>Server</td>
 *         <td>1st secret-part to en/decrypt the auth/Payload-Cookie</td>
 *     </tr>
 *     <tr>
 *         <td>Database</td>
 *         <td>2nd secret-part to en/decrypt the auth/Payload-Cookie, associated with the ID-Cookie value</td>
 *     </tr>
 * </table>
 * </p>
 * <p>
 * This hardens against the following "Leak of ..." attack scenarios:
 * <table>
 *     <tr>
 *         <th>Leak</th>
 *         <th>Mitigation</th>
 *     </tr>
 *     <tr>
 *         <td>client side cookies</td>
 *         <td>
 *             <ul>
 *                 <li>Cookies are encrypted</li>
 *                 <li>Cookies are constantly rotated on (re-)auth</li>
 *                 <li>Cookies have a short lifetime (usually a few days)</li>
 *             </ul>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>configuration (server)</td>
 *         <td>
 *             <ul>
 *                 <li>Security by design - Secret is not enough to decrypt the cookie</li>
 *             </ul>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>database</td>
 *         <td>
 *             <ul>
 *                 <li>DB-Secrets have a short lifetime and are constantly rotated</li>
 *                 <li>DB-Secret is not enough to decrypt the cookie</li>
 *             </ul>
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>source code</td>
 *         <td>
 *             <ul>
 *                 <li>Nothing is hardcoded here</li>
 *             </ul>
 *         </td>
 *     </tr>
 * </table>
 * </p>
 */
// There is a lot of split code related to saving and reading (required by interface).
// This code has common stuff which can't be abstracted usefully.
@SuppressWarnings("PMD.GodClass")
public class OAuth2CookieRememberMeServices implements RememberMeServices, OAuth2RefreshHandler
{
	private static final Logger LOG = LoggerFactory.getLogger(OAuth2CookieRememberMeServices.class);
	
	protected static final int DEFAULT_ID_COOKIE_VALUE_LENGTH = 32;
	// https://datatracker.ietf.org/doc/html/rfc6265#section-6.1
	protected static final int DEFAULT_COOKIE_VALUE_MAX_SIZE = 3950;
	
	protected final AutoLoginMetrics autoLoginMetrics;
	protected final OAuth2CookieRememberMeServicesConfig config;
	protected final RememberMeSymCryptManager cryptManager;
	protected final RememberMeClientStorageProcessorProvider clientStorageProcessorProvider;
	protected final AuthRememberMeSecretService authRememberMeSecretService;
	protected final OAuth2CookieRememberMeAuthSerializer payloadCookieAuthSerializer;
	protected final OAuth2AuthorizedClientService clientService;
	protected final ClientRegistrationRepository clientRegistrationRepository;
	protected final OAuth2AuthChecker oAuth2AuthChecker;
	protected final CookieSecureService cookieSecureService;
	
	protected final boolean enabled;
	
	@SuppressWarnings("java:S2629")
	public OAuth2CookieRememberMeServices(
		final OAuth2CookieRememberMeServicesConfig config,
		final AutoLoginMetrics autoLoginMetrics,
		final RememberMeSymCryptManager cryptManager,
		final RememberMeClientStorageProcessorProvider clientStorageProcessorProvider,
		final AuthRememberMeSecretService authRememberMeSecretService,
		final OAuth2CookieRememberMeAuthSerializer payloadCookieAuthSerializer,
		final OAuth2AuthorizedClientService clientService,
		final ClientRegistrationRepository clientRegistrationRepository,
		final OAuth2AuthChecker oAuth2AuthChecker,
		final CookieSecureService cookieSecureService)
	{
		this.config = config;
		this.autoLoginMetrics = autoLoginMetrics;
		this.clientStorageProcessorProvider = clientStorageProcessorProvider;
		this.authRememberMeSecretService = authRememberMeSecretService;
		this.payloadCookieAuthSerializer = payloadCookieAuthSerializer;
		this.clientService = clientService;
		this.clientRegistrationRepository = clientRegistrationRepository;
		this.oAuth2AuthChecker = oAuth2AuthChecker;
		this.cookieSecureService = cookieSecureService;
		
		this.cryptManager = this.config.isEnabled() ? cryptManager : null;
		
		this.enabled = config.isEnabled() && this.cryptManager != null;
		
		LOG.info("Instantiated with {}", this.config);
		if(!this.enabled)
		{
			LOG.info(
				"OAuth2CookieRememberMeServices are disabled; Reason: {}",
				!config.isEnabled()
					? config.disabledReason()
					: "No cryptManager");
		}
	}
	
	// region AutoLogin
	protected RequestMatcher ignoreRequestMatcher = r -> false;
	protected UnaryOperator<OAuth2User> enrichUserOnLoad = UnaryOperator.identity();
	
	public OAuth2CookieRememberMeServices setIgnoreRequestMatcher(final RequestMatcher ignoreRequestMatcher)
	{
		this.ignoreRequestMatcher = Objects.requireNonNull(ignoreRequestMatcher);
		return this;
	}
	
	public OAuth2CookieRememberMeServices setEnrichUserOnLoad(final UnaryOperator<OAuth2User> enrichUserOnLoad)
	{
		this.enrichUserOnLoad = Objects.requireNonNull(enrichUserOnLoad);
		return this;
	}
	
	@Override
	public Authentication autoLogin(final HttpServletRequest request, final HttpServletResponse response)
	{
		if(this.ignoreRequestMatcher.matches(request))
		{
			this.autoLoginMetrics.ignored();
			return null;
		}
		
		// Get both cookies or invalidate
		final Optional<Cookie> optIdCookie =
			FastCookieFinder.findCookie(request, this.config.getIdCookieName());
		final Optional<Cookie> optPayloadCookie =
			FastCookieFinder.findCookie(request, this.config.getPayloadCookieName());
		if(optIdCookie.isEmpty() || optPayloadCookie.isEmpty())
		{
			optIdCookie.ifPresentOrElse(
				c -> this.deleteCookie(c, response),
				() -> optPayloadCookie.ifPresent(c -> this.deleteCookie(c, response)));
			this.autoLoginMetrics.incompleteCookies();
			return null;
		}
		
		final Cookie idCookie = optIdCookie.get();
		final Cookie payloadCookie = optPayloadCookie.get();
		
		try
		{
			return this.autoLoginWithCookiesLock(request, response, idCookie, payloadCookie);
		}
		catch(final Exception ex)
		{
			if(ex instanceof AutoLoginException)
			{
				LOG.debug("Failed to restore auth from cookie", ex);
			}
			else
			{
				LOG.warn("Unexpected error while restoring auth from cookie", ex);
				this.autoLoginMetrics.unexpectedError();
			}
			
			// Invalidate cookies
			this.deleteCookie(idCookie, response);
			this.deleteCookie(payloadCookie, response);
			
			return null;
		}
	}
	
	protected final EnsureNonConcurrentExec<String, OAuth2AuthenticationToken> autoLoginEnsureNonConcurrentExec =
		new EnsureNonConcurrentExec<>(rex -> new EnsureNonConcurrentExec.SavedResult<>(null));
	
	protected OAuth2AuthenticationToken autoLoginWithCookiesLock(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final Cookie idCookie,
		final Cookie payloadCookie)
	{
		final String idCookieValue = this.decodeIdCookie(idCookie)
			.orElseThrow(() -> new AutoLoginException(
				this.autoLoginMetrics::idCookieDecodeFailed,
				"Failed to decode id cookie"));
		
		return this.autoLoginEnsureNonConcurrentExec.execute(
			idCookieValue,
			i -> this.autoLoginWithCookies(request, response, i, payloadCookie));
	}
	
	@SuppressWarnings("PMD.AvoidRethrowingException") // Required as there is another catch clause
	protected OAuth2AuthenticationToken autoLoginWithCookies(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final String idCookieValue,
		final Cookie payloadCookie)
	{
		final AuthRememberMeSecret authRememberMeSecret =
			this.authRememberMeSecretService.findByIdentifier(
					idCookieValue,
					LocalDateTime.now(ZoneOffset.UTC).minus(this.config.getExpiration()))
				.orElseThrow(() -> new AutoLoginException(
					this.autoLoginMetrics::persistedSecretNotFound,
					"Unable to find persisted secret"));
		
		final RememberMeSymCryptorProvider symCryptorProvider =
			this.cryptManager.forDecryption(authRememberMeSecret.cryptoAlgorithm())
				.orElseThrow(() -> new AutoLoginException(
					this.autoLoginMetrics::decryptionAlgorithmNotFound,
					"Unable to find decryption algorithm '" + authRememberMeSecret.cryptoAlgorithm() + "'"));
		
		final RestoredOAuth2AuthenticationToken auth;
		final OAuth2AuthorizedClient client;
		try
		{
			final OAuth2CookieRememberMeAuthSerializer.OAuth2AuthContainer container =
				this.payloadCookieAuthSerializer.deserialize(
					this.createCookieValueProcessor(authRememberMeSecret, symCryptorProvider)
						.readValue(payloadCookie.getValue()),
					clientId -> {
						final ClientRegistration clientRegistration =
							this.clientRegistrationRepository.findByRegistrationId(clientId);
						if(clientRegistration == null)
						{
							throw new AutoLoginException(
								this.autoLoginMetrics::payloadClientRegIdMismatch,
								"Unknown clientRegistrationId '" + clientId + "'");
						}
						return clientRegistration;
					});
			auth = new RestoredOAuth2AuthenticationToken(container.token());
			client = container.client();
		}
		catch(final AutoLoginException ale)
		{
			throw ale;
		}
		catch(final Exception ex)
		{
			throw new AutoLoginException(
				this.autoLoginMetrics::payloadDeserializeFailed,
				"Unable to deserialize",
				ex);
		}
		
		try
		{
			return this.autoLoginAfterCookieDecode(request, response, auth, client, authRememberMeSecret);
		}
		finally
		{
			this.authRememberMeSecretService.delete(authRememberMeSecret.identifier());
		}
	}
	
	protected OAuth2AuthenticationToken autoLoginAfterCookieDecode(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final RestoredOAuth2AuthenticationToken originalAuth,
		final OAuth2AuthorizedClient client,
		final AuthRememberMeSecret authRememberMeSecret)
	{
		// Do basic validation if restored values are e.g. inside bounds
		this.validateDecodedData(originalAuth, client, authRememberMeSecret);
		
		// Detailed check
		final OAuth2AuthChecker.AuthCheckResult authCheckResult =
			this.oAuth2AuthChecker.check(originalAuth, (regId, name) -> client);
		this.autoLoginMetrics.authCheckMetricsIncrement(authCheckResult.outcome());
		if(authCheckResult.outcome() == OAuth2AuthChecker.AuthCheckOutcome.DE_AUTH)
		{
			throw new AutoLoginException(null, "AuthCheckResult DE-AUTH");
		}
		
		// Try to enrich the data as if the user is initially logging in
		final OAuth2User enrichedUser;
		try
		{
			enrichedUser = this.enrichUserOnLoad.apply(originalAuth.getPrincipal());
		}
		catch(final OAuth2AuthenticationException ex)
		{
			throw new AutoLoginException(null, "Failed to enrich user", ex);
		}
		
		final RestoredOAuth2AuthenticationToken enrichedAuth =
			new RestoredOAuth2AuthenticationToken(originalAuth, enrichedUser);
		
		// Re-Encrypt
		LOG.debug("Restored auth[principal='{}'] from cookie; Re-Encrypting...", enrichedAuth.getName());
		
		final OAuth2AuthorizedClient clientToSave =
			authCheckResult instanceof final OAuth2AuthChecker.AccessTokenRefreshAuthCheckResult
				accessTokenRefreshResult
				? accessTokenRefreshResult.newClient()
				: client;
		this.clientService.saveAuthorizedClient(clientToSave, enrichedAuth);
		this.saveAuthToCookie(request, response, enrichedAuth, clientToSave, false);
		
		return enrichedAuth;
	}
	
	protected void validateDecodedData(
		final OAuth2AuthenticationToken token,
		final OAuth2AuthorizedClient client,
		final AuthRememberMeSecret authRememberMeSecret)
	{
		// token.getAuthorizedClientRegistrationId() was already validate during deserialization
		final String email = OAuth2AuthenticationTokenUtil.getEmailAttribute(token);
		if(!authRememberMeSecret.userEmailAddress().equals(email))
		{
			throw new AutoLoginException(
				this.autoLoginMetrics::payloadEmailMismatch,
				"Expected email['"
					+ authRememberMeSecret.userEmailAddress()
					+ "'] doesn't match found one['"
					+ email
					+ "']");
		}
		if(!this.isOAuth2TokenUsable(client.getAccessToken(), true))
		{
			throw new AutoLoginException(
				this.autoLoginMetrics::payloadAccessTokenInvalid,
				"AccessToken["
					+ Optional.ofNullable(client.getAccessToken())
					.map(t -> "expiredAt=" + t.getExpiresAt())
					.orElse(null)
					+ "] is not use able");
		}
		if(!this.isOAuth2TokenUsable(client.getRefreshToken(), false))
		{
			throw new AutoLoginException(
				this.autoLoginMetrics::payloadRefreshTokenInvalid,
				"RefreshToken["
					+ Optional.ofNullable(client.getRefreshToken())
					.map(t -> "expiredAt=" + t.getExpiresAt())
					.orElse(null)
					+ "] is not usable");
		}
	}
	
	protected boolean isOAuth2TokenUsable(final AbstractOAuth2Token token, final boolean required)
	{
		if(required && token == null)
		{
			return false;
		}
		final Instant expiresAt = token.getExpiresAt();
		return expiresAt == null || expiresAt.plus(this.config.getExpiration()).isAfter(Instant.now());
	}
	
	public static class AutoLoginException extends RuntimeException
	{
		public AutoLoginException(final Runnable incrementer, final String message)
		{
			super(message);
			if(incrementer != null)
			{
				incrementer.run();
			}
		}
		
		public AutoLoginException(final Runnable incrementer, final String message, final Exception ex)
		{
			super(message, ex);
			if(incrementer != null)
			{
				incrementer.run();
			}
		}
	}
	// endregion
	
	// region Save
	
	@Override
	public void loginFail(final HttpServletRequest request, final HttpServletResponse response)
	{
		FastCookieFinder.findCookie(request, this.config.getIdCookieName())
			.ifPresent(c -> {
				this.decodeIdCookie(c).ifPresent(this.authRememberMeSecretService::delete);
				this.deleteCookie(c, response);
				// It's highly unlikely that the Payload-cookie is present when the Id-Cookie is missing
				// To preserve performance only try to delete it when Id-Cookie is present
				FastCookieFinder.findCookie(request, this.config.getPayloadCookieName())
					.ifPresent(c2 -> this.deleteCookie(c2, response));
			});
	}
	
	@Override
	public void logout(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final Authentication authentication)
	{
		this.pendingCookieSaves.remove(authentication);
		this.loginFail(request, response);
	}
	
	@Override
	public void loginSuccess(
		final HttpServletRequest request,
		final HttpServletResponse response,
		final Authentication successfulAuthentication)
	{
		this.saveAuthToCookie(request, response, successfulAuthentication, null, true);
	}
	
	protected final Map<Authentication, CookieSaveAction> pendingCookieSaves = new ConcurrentReferenceHashMap<>();
	protected final Random random = new SecureRandom();
	
	@Override
	public void saveAuthToCookie(
		final ServletRequest request,
		final ServletResponse response,
		final Authentication authentication,
		final OAuth2AuthorizedClient alreadyPresentClient,
		final boolean tryDeleteExisting)
	{
		if(!this.enabled)
		{
			return;
		}
		
		if(!(authentication instanceof final OAuth2AuthenticationToken auth))
		{
			LOG.debug("Unable to save - Invalid auth");
			return;
		}
		
		final OAuth2AuthorizedClient client = Optional.ofNullable(alreadyPresentClient)
			.orElseGet(() -> this.clientService.loadAuthorizedClient(
				auth.getAuthorizedClientRegistrationId(),
				auth.getPrincipal().getName()));
		if(client == null)
		{
			LOG.debug("Unable to save - No client");
			return;
		}
		if(client.getRefreshToken() == null)
		{
			LOG.warn(
				"Save warning - No refresh token[clientId='{}',id='{}']",
				auth.getAuthorizedClientRegistrationId(),
				auth.getName());
		}
		
		final String email = OAuth2AuthenticationTokenUtil.getEmailAttribute(auth);
		if(email == null)
		{
			LOG.warn("Unable to save - No email");
			return;
		}
		
		final RememberMeSymCryptorProvider symCryptorProvider = this.cryptManager.forEncryption();
		
		final AuthRememberMeSecret authRememberMeSecret = this.authRememberMeSecretService.createNew(
			// 62(=possible chars when alphanumeric)^32(=count) possibilities should be pretty safe...
			RandomStringUtils.random(this.idCookieValueLength(), 0, 0, true, true, null, this.random),
			this.cryptManager.standardEncryptionProviderIdentifier(),
			symCryptorProvider.createSecretKeyFrom(this.random),
			email);
		
		this.authRememberMeSecretService.insert(authRememberMeSecret);
		
		final CookieSaveAction saveAction = new CookieSaveAction(
			// ID
			this.buildCookie(
				this.config.getIdCookieName(),
				authRememberMeSecret.identifier()),
			// Payload
			this.buildCookie(
				this.config.getPayloadCookieName(),
				this.createCookieValueProcessor(authRememberMeSecret, symCryptorProvider).writeValue(
					this.payloadCookieAuthSerializer.serialize(auth, client))),
			tryDeleteExisting);
		
		final int payloadValueLength = saveAction.payload().getValue().length();
		if(payloadValueLength > this.cookieValueMaxSize())
		{
			LOG.warn(
				"Payload Cookie[length={},clientRegId='{}',id='{}'] likely too large for saving",
				payloadValueLength,
				auth.getAuthorizedClientRegistrationId(),
				auth.getName());
		}
		
		this.pendingCookieSaves.put(auth, saveAction);
		this.tryWritePendingCookieSave(request, response, auth);
	}
	
	protected Cookie buildCookie(final String name, final String value)
	{
		final Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(this.cookieSecureService.isSecure());
		cookie.setMaxAge((int)this.config.getExpiration().toSeconds());
		cookie.setPath("/");
		return cookie;
	}
	
	protected record CookieSaveAction(
		Cookie id,
		Cookie payload,
		boolean tryDeleteExisting
	)
	{
	}
	
	@Override
	public void tryWritePendingCookieSave(
		final ServletRequest request,
		final ServletResponse response,
		final OAuth2AuthenticationToken auth)
	{
		// Enabled check is not required since pendingCookieSaves will always be empty if enabled = false
		// Quick check: Is there a pending cookie?
		if(!this.pendingCookieSaves.containsKey(auth))
		{
			return;
		}
		
		if(request instanceof final HttpServletRequest httpRequest
			// XHR can only set cookies in the response when withCredentials = true
			// See also: https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest/withCredentials
			// -> The request must already contain a cookie (which means that withCredentials was set to true)
			// Usually the request should contain at least JSESSIONID as Cookie
			&& httpRequest.getHeader("Cookie") != null
			&& response instanceof final HttpServletResponse httpResponse)
		{
			// Only remove when we can set the cookie!
			Optional.ofNullable(this.pendingCookieSaves.remove(auth))
				.ifPresent(csa -> this.executeCookieSaveAction(httpRequest, httpResponse, csa));
		}
	}
	
	protected void executeCookieSaveAction(
		final HttpServletRequest httpServletRequest,
		final HttpServletResponse httpServletResponse,
		final CookieSaveAction cookieSaveAction)
	{
		if(cookieSaveAction.tryDeleteExisting())
		{
			// Try to invalidate the old cookie value
			FastCookieFinder.findCookie(httpServletRequest, this.config.getIdCookieName())
				.flatMap(this::decodeIdCookie)
				// Do not invalidate yourself
				.filter(idValue -> !cookieSaveAction.id().getValue().equals(idValue))
				.ifPresent(this.authRememberMeSecretService::delete);
		}
		
		httpServletResponse.addCookie(cookieSaveAction.id());
		httpServletResponse.addCookie(cookieSaveAction.payload());
		
		LOG.debug(
			"Saving cookie for auth[identifier='{}']",
			cookieSaveAction.id().getValue());
	}
	
	// endregion
	
	protected Optional<String> decodeIdCookie(final Cookie idCookie)
	{
		return Optional.ofNullable(idCookie.getValue())
			// Filter out invalid values
			.filter(s -> s.length() == this.idCookieValueLength())
			.filter(StringUtils::isAlphanumeric);
	}
	
	protected RememberMeClientStorageProcessor createCookieValueProcessor(
		final AuthRememberMeSecret secret,
		final RememberMeSymCryptorProvider provider)
	{
		return this.clientStorageProcessorProvider.create(provider.create(secret.secret()));
	}
	
	protected void deleteCookie(final Cookie cookie, final HttpServletResponse response)
	{
		// Expire cookie
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		
		LOG.debug("Expiring Cookie[name='{}']", cookie.getName());
	}
	
	public int idCookieValueLength()
	{
		return DEFAULT_ID_COOKIE_VALUE_LENGTH;
	}
	
	public int cookieValueMaxSize()
	{
		return DEFAULT_COOKIE_VALUE_MAX_SIZE;
	}
	
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	// region Install
	@SuppressWarnings("java:S112") // Thrown upstream
	public void install(final HttpSecurity http) throws Exception
	{
		if(!this.enabled)
		{
			return;
		}
		
		http.rememberMe(c -> c.rememberMeServices(this)
			.addObjectPostProcessor(new ObjectPostProcessor<RememberMeAuthenticationProvider>()
			{
				@SuppressWarnings("unchecked")
				@Override
				public <O extends RememberMeAuthenticationProvider> O postProcess(final O object)
				{
					return (O)new OAuth2CookieRememberMeAuthenticationProvider();
				}
			}));
		LOG.debug("Installed into configurer");
	}
	
	// Required for filtering correctly in provider
	protected static class RestoredOAuth2AuthenticationToken extends OAuth2AuthenticationToken
	{
		public RestoredOAuth2AuthenticationToken(final OAuth2AuthenticationToken copyFrom)
		{
			super(copyFrom.getPrincipal(), copyFrom.getAuthorities(), copyFrom.getAuthorizedClientRegistrationId());
			this.setDetails(copyFrom.getDetails());
		}
		
		public RestoredOAuth2AuthenticationToken(
			final OAuth2AuthenticationToken copyFrom,
			final OAuth2User enrichedUser)
		{
			super(enrichedUser, enrichedUser.getAuthorities(), copyFrom.getAuthorizedClientRegistrationId());
			this.setDetails(copyFrom.getDetails());
		}
	}
	
	
	protected static class OAuth2CookieRememberMeAuthenticationProvider extends RememberMeAuthenticationProvider
	{
		public OAuth2CookieRememberMeAuthenticationProvider()
		{
			super("X");
		}
		
		@Override
		public Authentication authenticate(final Authentication authentication)
		{
			return authentication;
		}
		
		@Override
		public boolean supports(final Class<?> authentication)
		{
			return RestoredOAuth2AuthenticationToken.class.isAssignableFrom(authentication);
		}
		
		@Override
		public void afterPropertiesSet()
		{
			// NO OP
		}
		
		@Override
		public void setMessageSource(final MessageSource messageSource)
		{
			// NO OP
		}
	}
	// endregion
}
