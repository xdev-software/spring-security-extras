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

import software.xdev.sse.oauth2.checkauth.OAuth2AuthChecker;


@SuppressWarnings("java:S1186") // Dummy
public class DummyAutoLoginMetrics implements AutoLoginMetrics
{
	@Override
	public void ignored()
	{
	}
	
	@Override
	public void incompleteCookies()
	{
	}
	
	@Override
	public void idCookieDecodeFailed()
	{
	}
	
	@Override
	public void persistedSecretNotFound()
	{
	}
	
	@Override
	public void decryptionAlgorithmNotFound()
	{
	}
	
	@Override
	public void payloadDeserializeFailed()
	{
	}
	
	@Override
	public void payloadClientRegIdMismatch()
	{
	}
	
	@Override
	public void payloadEmailMismatch()
	{
	}
	
	@Override
	public void payloadAccessTokenInvalid()
	{
	}
	
	@Override
	public void payloadRefreshTokenInvalid()
	{
	}
	
	@Override
	public void authCheckMetricsIncrement(final OAuth2AuthChecker.AuthCheckOutcome outcome)
	{
	}
	
	@Override
	public void unexpectedError()
	{
	}
}
