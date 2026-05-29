package software.xdev.sse.validation.caching;

import jakarta.validation.ValidationProviderResolver;
import jakarta.validation.spi.BootstrapState;


class DummyBootstrapState implements BootstrapState
{
	@Override
	public ValidationProviderResolver getValidationProviderResolver()
	{
		return null;
	}
	
	@Override
	public ValidationProviderResolver getDefaultValidationProviderResolver()
	{
		return null;
	}
}
