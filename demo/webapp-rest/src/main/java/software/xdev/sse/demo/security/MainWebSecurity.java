package software.xdev.sse.demo.security;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.savedrequest.NullRequestCache;

import software.xdev.spring.security.web.authentication.ui.advanced.AdvancedLoginPageAdapter;
import software.xdev.spring.security.web.authentication.ui.advanced.config.AdditionalOAuth2ClientProperties;
import software.xdev.sse.csp.CSPGenerator;
import software.xdev.sse.web.hsts.HstsApplier;


@EnableWebSecurity
@Configuration
@EnableConfigurationProperties(AdditionalOAuth2ClientProperties.class)
public class MainWebSecurity
{
	@SuppressWarnings("java:S4502") // See below
	@Bean(name = "mainSecurityFilterChainBean")
	public SecurityFilterChain configure(
		final HttpSecurity http,
		final CSPGenerator cspGenerator,
		final AdditionalOAuth2ClientProperties additionalOAuth2ClientProperties,
		final HstsApplier hstsApplier)
		throws Exception
	{
		http.with(
				new AdvancedLoginPageAdapter<>(http),
				c -> c
					.customizePages(p -> p.setHeaderElements(List.of(
						"<link href=\"/lib/bootstrap-5.3.3.min.css\" rel=\"stylesheet\"/>",
						"<link href=\"/lib/theme.css\" rel=\"stylesheet\"/>",
						"<script src=\"/lib/bootstrap-5.3.3.bundle.min.js\"></script>",
						"<script src=\"/lib/theme.js\"></script>"
					)))
					.customizeLoginPage(p -> p
						.additionalOAuth2RegistrationProperties(additionalOAuth2ClientProperties.getRegistration())
						.header("<div class='d-flex justify-content-center'>"
							+ "  <img src='/assets/XDEV_LOGO.svg' alt='XDEV' style='max-width:100%;"
							+ "height:calc(var(--bs-body-font-size) * 2.5)'></img>"
							+ "</div>"
							+ "<h2 class='h2 mb-3 text-center'>Demo</h2>")
					))
			.headers(h -> hstsApplier.apply(h)
				.referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
				// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options
				.contentTypeOptions(Customizer.withDefaults())
				.contentSecurityPolicy(csp -> csp.policyDirectives(cspGenerator.buildCSP())))
			.oauth2Login(c -> c.defaultSuccessUrl("/"))
			// Disable CSRF for REST API for demo purposes
			.csrf(c -> c.ignoringRequestMatchers("/api/**"))
			.authorizeHttpRequests(urlRegistry -> urlRegistry.anyRequest().authenticated())
			.logout(Customizer.withDefaults())
			// nothing needs to be saved
			.requestCache(r -> r.requestCache(new NullRequestCache()));
		
		return http.build();
	}
	
}
