package software.xdev.sse.oauth2.checkauth;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import software.xdev.sse.oauth2.checkauth.config.AuthProviderOfflineConfig;


public class MockOAuth2ProviderOfflineManager extends OAuth2ProviderOfflineManager
{
	private final AtomicReference<Instant> nowRef;
	
	public MockOAuth2ProviderOfflineManager(
		final AuthProviderOfflineConfig config,
		final AtomicReference<Instant> nowRef)
	{
		super(config, List.of(), true);
		this.nowRef = nowRef;
	}
	
	@Override
	protected Instant now()
	{
		return this.nowRef.get();
	}
}
