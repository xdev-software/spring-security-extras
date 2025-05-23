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
package software.xdev.sse.web.sidecar.actuator;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import software.xdev.sse.codec.hash.SHA256Hashing;
import software.xdev.sse.web.sidecar.actuator.config.ActuatorSecurityConfig;
import software.xdev.sse.web.sidecar.actuator.metrics.ActuatorSecurityMetricsHandler;


@ConditionalOnProperty(value = "sse.sidecar.actuator.enabled", matchIfMissing = true)
@EnableWebSecurity
@AutoConfiguration
public class ActuatorWebSecurity
{
	private static final Logger LOG = LoggerFactory.getLogger(ActuatorWebSecurity.class);
	
	protected final ActuatorSecurityConfig config;
	protected final List<ActuatorSecurityMetricsHandler> metricsHandlers;
	
	public ActuatorWebSecurity(
		final ActuatorSecurityConfig config,
		final List<ActuatorSecurityMetricsHandler> metricsHandlers)
	{
		this.config = config;
		this.metricsHandlers = metricsHandlers.stream()
			.filter(ActuatorSecurityMetricsHandler::enabled)
			.toList();
	}
	
	@Bean
	@Order(1)
	@SuppressWarnings("java:S4502")
	public SecurityFilterChain configureActuator(
		final WebEndpointProperties actuatorWebEndpointProperties,
		final HttpSecurity http) throws Exception
	{
		LOG.info("Building SecurityFilterChain with {}", this.config);
		
		final Set<String> alUserEndpoints = this.config.getUsers()
			.stream()
			.flatMap(u -> u.getAllowedEndpoints().stream())
			.filter(Objects::nonNull)
			.filter(s -> !s.isBlank())
			.collect(Collectors.toSet());
		
		return http
			.securityMatcher(actuatorWebEndpointProperties.getBasePath() + "/**")
			.authorizeHttpRequests(registry -> {
				alUserEndpoints.forEach(endpoint ->
					registry.requestMatchers("/actuator/" + endpoint)
						.hasAnyRole(this.config.getDefaultRoleName(), this.endpointToRole(endpoint)));
				registry.anyRequest().hasRole(this.config.getDefaultRoleName());
			})
			.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.httpBasic(NoErrorBasicAuthenticationEntryPoint::install)
			// NO CSRF required as we have BasicAuth anyway
			.csrf(AbstractHttpConfigurer::disable)
			.authenticationManager(new ProviderManager(this.createActuatorAuthProvider()))
			.build();
	}
	
	protected String endpointToRole(final String endpoint)
	{
		return this.config.getDefaultRoleName()
			+ "_"
			+ endpoint.trim().toUpperCase().replace("/", "_");
	}
	
	protected AuthenticationProvider createActuatorAuthProvider()
	{
		final List<UserDetails> users = this.config.getUsers()
			.stream()
			.map(au -> User.builder()
				.username(au.getUsername())
				.password(au.getPasswordSha256())
				.roles((au.getAllowedEndpoints().isEmpty()
					? Stream.of(this.config.getDefaultRoleName())
					: au.getAllowedEndpoints().stream().map(this::endpointToRole))
					.toArray(String[]::new))
				.build())
			.toList();
		final DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(
			new InMemoryUserDetailsManager(users));
		
		final int passwordMaxLength = this.config.getPasswordMaxLength();
		daoAuthenticationProvider.setPasswordEncoder(new PasswordEncoder()
		{
			@Override
			public boolean matches(final CharSequence rawPassword, final String encodedPassword)
			{
				if(rawPassword == null || rawPassword.isEmpty() || rawPassword.length() > passwordMaxLength)
				{
					ActuatorWebSecurity.this.metrics(ActuatorSecurityMetricsHandler::loginFailed);
					return false;
				}
				
				final boolean success = SHA256Hashing.hash(rawPassword.toString()).equals(encodedPassword);
				ActuatorWebSecurity.this.metrics(success
					? ActuatorSecurityMetricsHandler::loginSuccess
					: ActuatorSecurityMetricsHandler::loginFailed);
				
				return success;
			}
			
			@Override
			public String encode(final CharSequence rawPassword)
			{
				return rawPassword.toString();
			}
		});
		
		return daoAuthenticationProvider;
	}
	
	protected void metrics(final Consumer<ActuatorSecurityMetricsHandler> consumer)
	{
		this.metricsHandlers.forEach(consumer);
	}
}
