# Web Sidecar Actuator

Secures [Spring Boot's Actuator endpoints](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html).

For more details about sidecars please have a look at [``../web-sidecars``](../web-sidecars).

Please note that you must supply a configuration like this:
```java
@Autowired
MySystemConfig systemConfig;

@Bean
ActuatorConfig actuatorConfig() {
    return systemConfig.getActuatorConfig();
}

@Validated
@Configuration
@ConfigurationProperties(prefix = "myapp")
class MySystemConfig {
    @NotNull
    private ActuatorConfig actuator = new ActuatorConfig();

    // ...
}
```
