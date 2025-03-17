package software.xdev.sse.oauth2.filter.metrics;

import software.xdev.sse.oauth2.checkauth.OAuth2AuthChecker;


public class DummyOAuth2RefreshFilterAuthCheckMetrics implements OAuth2RefreshFilterAuthCheckMetrics
{
	@Override
	public void ignored()
	{
	}
	
	@Override
	public void noAuth()
	{
	}
	
	@Override
	public void authCheckMetricsIncrement(final OAuth2AuthChecker.AuthCheckOutcome outcome)
	{
	}
}
