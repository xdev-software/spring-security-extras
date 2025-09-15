package software.xdev.sse.demo;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SuppressWarnings({"checkstyle:HideUtilityClassConstructor", "PMD.UseUtilityClass"})
@SpringBootApplication
public class Application
{
	@SuppressWarnings("PMD.AvoidSystemSetterCall")
	public static void main(final String[] args)
	{
		System.setProperty(
			"spring.config.additional-location",
			Stream.of(
					"optional:classpath:/application-add.yml",
					"classpath:/application-add-log.yml",
					System.getProperty("spring.config.additional-location"),
					// [Deployment] Also consider ENV variable since it has a lower priority and is otherwise ignored
					// See https://docs.spring.io/spring-boot/reference/features/external-config.html
					System.getenv("SPRING_CONFIG_ADDITIONAL-LOCATION"))
				.filter(Objects::nonNull)
				.collect(Collectors.joining(",")));
		SpringApplication.run(Application.class, args);
	}
}
