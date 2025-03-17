package software.xdev.sse.oauth2.rememberme.metrics;

import software.xdev.sse.oauth2.checkauth.OAuth2AuthChecker;


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
