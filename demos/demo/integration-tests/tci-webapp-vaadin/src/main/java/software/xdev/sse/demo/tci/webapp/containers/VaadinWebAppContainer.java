package software.xdev.sse.demo.tci.webapp.containers;

@SuppressWarnings("java:S2160")
public class VaadinWebAppContainer extends WebAppContainer<VaadinWebAppContainer>
{
	public VaadinWebAppContainer(final String dockerImageName, final boolean connectionlessStart)
	{
		super(dockerImageName, connectionlessStart);
	}
	
	public VaadinWebAppContainer withDefaultPayloadEncryption()
	{
		return this.withRememberMePayloadEncryption("JustForDev_1");
	}
	
	public VaadinWebAppContainer withRememberMePayloadEncryption(final String chacha20Nonce)
	{
		final String prefix = "SSE_AUTH_REMEMBER-ME_PAYLOAD-ENCRYPTION_";
		return this.withEnv(prefix + "STANDARD", "v2")
			.withEnv(prefix + "CHACHA20_v2_NONCE", chacha20Nonce)
			// Legacy
			.withEnv(prefix + "AESGCM_legacy_INIT-VECTOR", "RandomInitIV")
			.withEnv(prefix + "AESGCM_legacy_SECRET-KEY-LENGTH", "16");
	}
}
