package software.xdev.sse.validation.caching;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.validation.MessageInterpolator;

import org.hibernate.validator.internal.engine.ConfigurationImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.validation.MessageInterpolatorFactory;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;


class WrappingCacheableConfigurationTest
{
	@Test
	void checkDetermineConstraintValidatorFactoryObjForSpring()
	{
		final ConfigurationImpl configuration = new ConfigurationImpl(new DummyBootstrapState());
		final var config = new WrappingCacheableConfiguration(configuration, null);
		
		final SpringConstraintValidatorFactory springConstraintValidatorFactory = new SpringConstraintValidatorFactory(
			new DefaultListableBeanFactory(),
			configuration.getDefaultConstraintValidatorFactory());
		
		assertEquals(
			SpringConstraintValidatorFactory.class,
			config.determineConstraintValidatorFactoryObj(springConstraintValidatorFactory)
		);
	}
	
	@Test
	void checkDetermineMessageInterpolatorObjForSpring()
	{
		final var config = new WrappingCacheableConfiguration(
			new ConfigurationImpl(new DummyBootstrapState()),
			null);
		
		final StaticMessageSource messageSource = new StaticMessageSource();
		final MessageInterpolator messageInterpolator =
			new LocaleContextMessageInterpolator(new MessageInterpolatorFactory(messageSource).getObject());
		
		assertEquals(
			messageSource,
			config.determineMessageInterpolatorObj(messageInterpolator)
		);
	}
}
