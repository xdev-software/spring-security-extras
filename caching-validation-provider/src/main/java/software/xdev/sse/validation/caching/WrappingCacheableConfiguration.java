package software.xdev.sse.validation.caching;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.validation.BootstrapConfiguration;
import jakarta.validation.ClockProvider;
import jakarta.validation.Configuration;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.ParameterNameProvider;
import jakarta.validation.TraversableResolver;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.valueextraction.ValueExtractor;

import org.hibernate.validator.internal.engine.ConfigurationImpl;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;


public class WrappingCacheableConfiguration implements Configuration<WrappingCacheableConfiguration>
{
	private static final Logger LOG = LoggerFactory.getLogger(WrappingCacheableConfiguration.class);
	
	private ConfigurationImpl configuration;
	private final CachingValidationProvider cachingValidationProvider;
	
	private boolean ignoreXmlConfiguration;
	private Object messageInterpolatorObj;
	private TraversableResolver traversableResolver;
	private Object constraintValidatorFactoryObj;
	private ParameterNameProvider parameterNameProvider;
	private ClockProvider clockProvider;
	private final Set<ValueExtractor<?>> valueExtractors = new HashSet<>();
	private final Set<InputStream> mappings = new HashSet<>();
	private final Map<String, String> properties = new HashMap<>();
	
	public WrappingCacheableConfiguration(
		final ConfigurationImpl configuration,
		final CachingValidationProvider cachingValidationProvider)
	{
		this.configuration = configuration;
		this.cachingValidationProvider = cachingValidationProvider;
	}
	
	@Override
	public WrappingCacheableConfiguration ignoreXmlConfiguration()
	{
		this.ignoreXmlConfiguration = true;
		this.configuration.ignoreXmlConfiguration();
		return this;
	}
	
	@Override
	public WrappingCacheableConfiguration messageInterpolator(final MessageInterpolator interpolator)
	{
		try
		{
			this.messageInterpolatorObj = this.determineMessageInterpolatorObj(interpolator);
		}
		catch(final Exception ex)
		{
			LOG.warn("Failed to determineMessageInterpolatorObj", ex);
			this.messageInterpolatorObj = interpolator;
		}
		
		this.configuration.messageInterpolator(interpolator);
		return this;
	}
	
	protected Object determineMessageInterpolatorObj(
		final MessageInterpolator interpolator)
	{
		// Check if this uses the same internal message source,
		// the rest is irrelevant as it's always identical by default
		if(interpolator instanceof final LocaleContextMessageInterpolator localeContextMessageInterpolator)
		{
			final MessageInterpolator targetInterpolator = FieldAccessor.get(
				LocaleContextMessageInterpolator.class,
				"targetInterpolator",
				localeContextMessageInterpolator,
				MessageInterpolator.class
			);
			final Class<?> clazz;
			try
			{
				clazz = Class.forName("org.springframework.boot.validation.MessageSourceMessageInterpolator");
			}
			catch(final ClassNotFoundException e)
			{
				return interpolator;
			}
			
			if(clazz.isInstance(targetInterpolator)
				&& FieldAccessor.get(
				clazz,
				"messageInterpolator",
				targetInterpolator,
				MessageInterpolator.class) instanceof ResourceBundleMessageInterpolator)
			{
				return FieldAccessor.get(clazz, "messageSource", targetInterpolator, Object.class);
			}
		}
		
		return interpolator;
	}
	
	@Override
	public WrappingCacheableConfiguration traversableResolver(final TraversableResolver resolver)
	{
		this.traversableResolver = resolver;
		this.configuration.traversableResolver(resolver);
		return this;
	}
	
	@Override
	public WrappingCacheableConfiguration constraintValidatorFactory(
		final ConstraintValidatorFactory constraintValidatorFactory)
	{
		try
		{
			this.constraintValidatorFactoryObj =
				this.determineConstraintValidatorFactoryObj(constraintValidatorFactory);
		}
		catch(final Exception ex)
		{
			LOG.warn("Failed to determineConstraintValidatorFactoryObj", ex);
			this.constraintValidatorFactoryObj = constraintValidatorFactory;
		}
		this.configuration.constraintValidatorFactory(constraintValidatorFactory);
		return this;
	}
	
	protected Object determineConstraintValidatorFactoryObj(
		final ConstraintValidatorFactory constraintValidatorFactory)
	{
		// Spring fills in the defaultConstraintValidatorFactory into it's wrapper that it always re-creates
		if(constraintValidatorFactory instanceof final SpringConstraintValidatorFactory springConstValFactory
			&& Objects.equals(
			this.getDefaultConstraintValidatorFactory(),
			FieldAccessor.get(
				SpringConstraintValidatorFactory.class,
				"defaultConstraintValidatorFactory",
				springConstValFactory,
				ConstraintValidatorFactory.class)))
		{
			return SpringConstraintValidatorFactory.class;
		}
		
		return constraintValidatorFactory;
	}
	
	@Override
	public WrappingCacheableConfiguration parameterNameProvider(final ParameterNameProvider parameterNameProvider)
	{
		this.parameterNameProvider = parameterNameProvider;
		this.configuration.parameterNameProvider(parameterNameProvider);
		return this;
	}
	
	@Override
	public WrappingCacheableConfiguration clockProvider(final ClockProvider clockProvider)
	{
		this.clockProvider = clockProvider;
		this.configuration.clockProvider(clockProvider);
		return this;
	}
	
	@Override
	public WrappingCacheableConfiguration addValueExtractor(final ValueExtractor<?> extractor)
	{
		this.valueExtractors.add(extractor);
		this.configuration.addValueExtractor(extractor);
		return this;
	}
	
	@Override
	public WrappingCacheableConfiguration addMapping(final InputStream stream)
	{
		this.mappings.add(stream);
		this.configuration.addMapping(stream);
		return this;
	}
	
	@Override
	public WrappingCacheableConfiguration addProperty(final String name, final String value)
	{
		this.properties.put(name, value);
		this.configuration.addProperty(name, value);
		return this;
	}
	
	@Override
	public MessageInterpolator getDefaultMessageInterpolator()
	{
		return this.configuration.getDefaultMessageInterpolator();
	}
	
	@Override
	public TraversableResolver getDefaultTraversableResolver()
	{
		return this.configuration.getDefaultTraversableResolver();
	}
	
	@Override
	public ConstraintValidatorFactory getDefaultConstraintValidatorFactory()
	{
		return this.configuration.getDefaultConstraintValidatorFactory();
	}
	
	@Override
	public ParameterNameProvider getDefaultParameterNameProvider()
	{
		return this.configuration.getDefaultParameterNameProvider();
	}
	
	@Override
	public ClockProvider getDefaultClockProvider()
	{
		return this.configuration.getDefaultClockProvider();
	}
	
	@Override
	public BootstrapConfiguration getBootstrapConfiguration()
	{
		return this.configuration.getBootstrapConfiguration();
	}
	
	@Override
	public ValidatorFactory buildValidatorFactory()
	{
		final ConfigurationImpl config = this.configuration;
		this.configuration = null; // Free up memory
		return this.cachingValidationProvider.getValidatorFactory(this, config);
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(!(o instanceof final WrappingCacheableConfiguration that))
		{
			return false;
		}
		return this.ignoreXmlConfiguration == that.ignoreXmlConfiguration
			&& Objects.equals(this.messageInterpolatorObj, that.messageInterpolatorObj)
			&& Objects.equals(this.traversableResolver, that.traversableResolver)
			&& Objects.equals(this.constraintValidatorFactoryObj, that.constraintValidatorFactoryObj)
			&& Objects.equals(this.parameterNameProvider, that.parameterNameProvider)
			&& Objects.equals(this.clockProvider, that.clockProvider)
			&& Objects.equals(this.valueExtractors, that.valueExtractors)
			&& Objects.equals(this.mappings, that.mappings)
			&& Objects.equals(this.properties, that.properties);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(
			this.ignoreXmlConfiguration,
			this.messageInterpolatorObj,
			this.traversableResolver,
			this.constraintValidatorFactoryObj,
			this.parameterNameProvider,
			this.clockProvider,
			this.valueExtractors,
			this.mappings,
			this.properties);
	}
}
