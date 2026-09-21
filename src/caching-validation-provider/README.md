# Caching Validation Provider

Utility to reduce instantiation of Validators.

The underlying problem can be observed when enabling `DEBUG` logging for `org.hibernate.validator` and was described in [spring-projects/spring-boot#50597](https://github.com/spring-projects/spring-boot/issues/50597).
The Validator(Factory) get's instantiated a lot:
1. By `JakartaValidationBackgroundPreinitializer`
2. By Hibernate itself when `spring.jpa.properties.jakarta.persistence.validation.mode` is NOT set to `none`
3. For every `@Validated` annotation in `ConfigurationPropertiesBinder` (in SSE this can happen up to 8 times)
4. As `defaultValidator` in `LocalValidatorFactoryBean`

The problem is worked around by:
* Caching ValidatorFactories for identical configurations
  * Wrapping configuration in a comparable object so that it can be determined if it's identical or not
* Disabling XML configuration by default (can be re-enabled by setting system property `validation.xml-configuration.enable=true`)

NOTE: The code here is not directly related to SSE (it hooks itself into `jakarta.validation.spi.ValidationProvider` to patch the observed problems) but it - for now - lives here because SSE is heavily affected by it.
