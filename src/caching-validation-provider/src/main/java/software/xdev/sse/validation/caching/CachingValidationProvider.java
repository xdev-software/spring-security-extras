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
package software.xdev.sse.validation.caching;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.validation.Configuration;
import jakarta.validation.TraversableResolver;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.spi.BootstrapState;
import jakarta.validation.spi.ConfigurationState;
import jakarta.validation.spi.ValidationProvider;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.internal.engine.AbstractConfigurationImpl;
import org.hibernate.validator.internal.engine.ConfigurationImpl;
import org.hibernate.validator.internal.engine.resolver.TraversableResolvers;
import org.hibernate.validator.internal.xml.config.ValidationBootstrapParameters;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CachingValidationProvider extends HibernateValidator
{
	private static final Logger LOG = LoggerFactory.getLogger(CachingValidationProvider.class);
	
	final BootstrapState noOpBootstrapState = new DummyBootstrapState();
	
	TraversableResolver defaultTraversableResolver;
	
	public CachingValidationProvider()
	{
		this(true);
	}
	
	CachingValidationProvider(final boolean logInstantiation)
	{
		if(logInstantiation)
		{
			LOG.debug("Instantiated");
		}
	}
	
	protected ConfigurationImpl installDefaultTraversableResolver(final ConfigurationImpl configImpl)
	{
		try
		{
			if(this.defaultTraversableResolver == null)
			{
				this.defaultTraversableResolver = TraversableResolvers.getDefault();
			}
			
			FieldAccessor.set(
				AbstractConfigurationImpl.class,
				"defaultTraversableResolver",
				configImpl,
				this.defaultTraversableResolver
			);
		}
		catch(final Exception ex)
		{
			LOG.warn("Failed to set defaultTraversableResolver", ex);
		}
		return configImpl;
	}
	
	@Override
	public Configuration<?> createGenericConfiguration(final BootstrapState state)
	{
		final WrappingCacheableConfiguration wrappedCached = new WrappingCacheableConfiguration(
			this.installDefaultTraversableResolver(new ConfigurationImpl(this.noOpBootstrapState)),
			this);
		
		if(!Boolean.getBoolean("validation.xml-configuration.enable"))
		{
			wrappedCached.ignoreXmlConfiguration();
		}
		
		return wrappedCached;
	}
	
	private final Map<WrappingCacheableConfiguration, ValidatorFactory> cachedFactories =
		new ConcurrentHashMap<>();
	
	public ValidatorFactory getValidatorFactory(
		final WrappingCacheableConfiguration wrappingCacheableConfiguration,
		@Nullable final ConfigurationImpl configuration)
	{
		final int hashCode = wrappingCacheableConfiguration.hashCode();
		LOG.debug("getValidatorFactory called for config[hashCode={}]", hashCode);
		return this.cachedFactories.computeIfAbsent(
			wrappingCacheableConfiguration,
			c -> {
				LOG.debug("Building new ValidatorFactory for config[hashCode={}]", hashCode);
				Objects.requireNonNull(configuration);
				
				// Execute internal configuration
				// Trick doing this by temporarily using a non-functional ValidationProvider
				try
				{
					final ValidationBootstrapParameters validationBootstrapParameters = FieldAccessor.get(
						AbstractConfigurationImpl.class,
						"validationBootstrapParameters",
						configuration,
						ValidationBootstrapParameters.class);
					validationBootstrapParameters.setProvider(new NoOpValidationProvider());
					configuration.buildValidatorFactory();
					validationBootstrapParameters.setProvider(null);
				}
				catch(final Exception ex)
				{
					LOG.warn("Failed to do initial getValidatorFactory", ex);
				}
				
				return this.buildValidatorFactory(configuration);
			});
	}
	
	static class NoOpValidationProvider implements ValidationProvider<HibernateValidatorConfiguration>
	{
		@Override
		public ConfigurationImpl createSpecializedConfiguration(final BootstrapState state)
		{
			return null;
		}
		
		@Override
		public Configuration<?> createGenericConfiguration(final BootstrapState state)
		{
			return null;
		}
		
		@Override
		public ValidatorFactory buildValidatorFactory(final ConfigurationState configurationState)
		{
			return null;
		}
	}
}
