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
package software.xdev.sse.oauth2.rememberme.metrics;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import software.xdev.sse.metrics.SSESharedMetrics;
import software.xdev.sse.oauth2.checkauth.OAuth2AuthChecker;


public class DefaultAutoLoginMetrics implements AutoLoginMetrics
{
	private final Counter ignored;
	private final Counter incompleteCookies;
	private final Counter idCookieDecodeFailed;
	private final Counter persistedSecretNotFound;
	private final Counter decryptionAlgorithmNotFound;
	private final Counter payloadDeserializeFailed;
	private final Counter payloadClientRegIdMismatch;
	private final Counter payloadEmailMismatch;
	private final Counter payloadAccessTokenInvalid;
	private final Counter payloadRefreshTokenInvalid;
	private final Map<OAuth2AuthChecker.AuthCheckOutcome, Counter> authCheckMetrics;
	private final Counter unexpectedError;
	
	public DefaultAutoLoginMetrics(final MeterRegistry meterRegistry)
	{
		final Function<String, Counter> builder = o -> meterRegistry.counter(
			SSESharedMetrics.PREFIX + "remember_me_auto_login",
			SSESharedMetrics.TAG_OUTCOME,
			o);
		
		this.ignored = builder.apply("ignored");
		this.incompleteCookies = builder.apply("incomplete_cookies");
		this.idCookieDecodeFailed = builder.apply("id_cookie_decode_failed");
		this.persistedSecretNotFound = builder.apply("persisted_secret_not_found");
		this.decryptionAlgorithmNotFound = builder.apply("decryption_algorithm_not_found");
		this.payloadDeserializeFailed = builder.apply("payload_deserialize_failed");
		this.payloadClientRegIdMismatch = builder.apply("payload_client_reg_id_mismatch");
		this.payloadEmailMismatch = builder.apply("payload_email_mismatch");
		this.payloadAccessTokenInvalid = builder.apply("payload_access_token_invalid");
		this.payloadRefreshTokenInvalid = builder.apply("payload_refresh_token_invalid");
		this.authCheckMetrics = Arrays.stream(OAuth2AuthChecker.AuthCheckOutcome.values())
			.collect(Collectors.toMap(Function.identity(), o -> builder.apply(o.metricsOutcome())));
		this.unexpectedError = builder.apply("unexpected_error");
	}
	
	@Override
	public void ignored()
	{
		this.ignored.increment();
	}
	
	@Override
	public void incompleteCookies()
	{
		this.incompleteCookies.increment();
	}
	
	@Override
	public void idCookieDecodeFailed()
	{
		this.idCookieDecodeFailed.increment();
	}
	
	@Override
	public void persistedSecretNotFound()
	{
		this.persistedSecretNotFound.increment();
	}
	
	@Override
	public void decryptionAlgorithmNotFound()
	{
		this.decryptionAlgorithmNotFound.increment();
	}
	
	@Override
	public void payloadDeserializeFailed()
	{
		this.payloadDeserializeFailed.increment();
	}
	
	@Override
	public void payloadClientRegIdMismatch()
	{
		this.payloadClientRegIdMismatch.increment();
	}
	
	@Override
	public void payloadEmailMismatch()
	{
		this.payloadEmailMismatch.increment();
	}
	
	@Override
	public void payloadAccessTokenInvalid()
	{
		this.payloadAccessTokenInvalid.increment();
	}
	
	@Override
	public void payloadRefreshTokenInvalid()
	{
		this.payloadRefreshTokenInvalid.increment();
	}
	
	@Override
	public void authCheckMetricsIncrement(final OAuth2AuthChecker.AuthCheckOutcome outcome)
	{
		this.authCheckMetrics.get(outcome).increment();
	}
	
	@Override
	public void unexpectedError()
	{
		this.unexpectedError.increment();
	}
}
