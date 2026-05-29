package software.xdev.sse.validation.caching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.validation.TraversableResolver;

import org.hibernate.validator.internal.engine.ConfigurationImpl;
import org.junit.jupiter.api.Test;


class CachingValidationProviderTest
{
	@Test
	void checkInstallDefaultTraversableResolver()
	{
		final ConfigurationImpl configuration = new ConfigurationImpl(new DummyBootstrapState());
		final CachingValidationProvider cachingValidationProvider = new CachingValidationProvider(false);
		
		cachingValidationProvider.installDefaultTraversableResolver(configuration);
		
		final TraversableResolver defaultTraversableResolver = cachingValidationProvider.defaultTraversableResolver;
		
		assertNotNull(defaultTraversableResolver);
		assertEquals(configuration.getDefaultTraversableResolver(), defaultTraversableResolver);
	}
}
