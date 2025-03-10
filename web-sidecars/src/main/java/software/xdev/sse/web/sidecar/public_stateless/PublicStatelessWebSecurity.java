package software.xdev.sse.web.sidecar.public_stateless;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@Configuration
public class PublicStatelessWebSecurity
{
	@Bean
	@Order(10)
	@SuppressWarnings("java:S4502")
	public SecurityFilterChain configureStaticResources(
		final List<PublicStatelessPathsProvider> publicStatelessPathsProviders,
		final HttpSecurity http) throws Exception
	{
		// Static resources that require no authentication
		return http
			// Alternative:
			// Use WebMvcConfigurer#addResourceHandlers
			// registry.setOrder(1) - So that it's executed before (Vaadin)Servlet
			// spring.web.resources.add-mappings=false - Disable defaults
			.securityMatcher(publicStatelessPathsProviders.stream()
				.filter(PublicStatelessPathsProvider::enabled)
				.map(PublicStatelessPathsProvider::paths)
				.flatMap(Collection::stream)
				.distinct()
				.toArray(String[]::new))
			.authorizeHttpRequests(a -> a.anyRequest().permitAll())
			// NO CSRF required as these resources are publicly available
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.build();
	}
}
