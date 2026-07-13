package software.xdev.sse.demo.persistence.config;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SuppressWarnings("java:S1118") // This is not a utility class
@EnableJpaRepositories("software.xdev.sse.demo.persistence.jpa.dao")
public abstract class DefaultJPAConfig
{
	public static final String ENTITY_PACKAGE = "software.xdev.sse.demo.entities";
}
