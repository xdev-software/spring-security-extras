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
