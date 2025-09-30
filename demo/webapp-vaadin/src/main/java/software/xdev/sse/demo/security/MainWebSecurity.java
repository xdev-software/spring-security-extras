package software.xdev.sse.demo.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import software.xdev.spring.security.web.authentication.ui.advanced.AdvancedLoginPageAdapter;
import software.xdev.sse.csp.CSPGenerator;
import software.xdev.sse.demo.ui.structure.MainView;
import software.xdev.sse.oauth2.filter.OAuth2RefreshFilter;
import software.xdev.sse.oauth2.loginurl.OAuth2LoginUrlStoreAdapter;
import software.xdev.sse.oauth2.rememberloginproviderredirect.CookieBasedRememberRedirectOAuth2LoginProvider;
import software.xdev.sse.oauth2.rememberme.OAuth2CookieRememberMeServices;
import software.xdev.sse.vaadin.TotalVaadinFlowSecurityConfigurer;


@EnableWebSecurity
@Configuration
public class MainWebSecurity
{
	private static final Logger LOG = LoggerFactory.getLogger(MainWebSecurity.class);
	
	@Bean
	protected SecurityFilterChain httpSecurityFilterChain(
		final HttpSecurity http,
		final OAuth2CookieRememberMeServices cookieRememberMeServices,
		final OAuth2RefreshFilter oAuth2RefreshFilter,
		final CSPGenerator cspGenerator,
		final CookieBasedRememberRedirectOAuth2LoginProvider rememberLoginProvider,
		final OAuth2LoginUrlStoreAdapter oAuth2LoginUrlStoreAdapter) throws Exception
	{
		http
			.with(new AdvancedLoginPageAdapter<>(http), c -> c
				.customizePages(p -> p.setHeaderElements(List.of(
					"<link href=\"/lib/bootstrap-5.3.3.min.css\" rel=\"stylesheet\"/>",
					"<link href=\"/lib/theme.css\" rel=\"stylesheet\"/>",
					"<script src=\"/lib/bootstrap-5.3.3.bundle.min.js\"></script>",
					"<script src=\"/lib/theme.js\"></script>"
				))))
			// Permission-Policy removed as it's not supported by browsers (besides Chrome)
			// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Permissions-Policy#browser_compatibility
			.headers(c -> c
				.contentSecurityPolicy(p -> p.policyDirectives(cspGenerator.buildCSP()))
				// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options
				.contentTypeOptions(Customizer.withDefaults())
				.referrerPolicy(p -> p.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)))
			.oauth2Login(c -> {
				c.defaultSuccessUrl("/" + MainView.NAV);
				rememberLoginProvider.configureOAuth2Login(c);
				oAuth2LoginUrlStoreAdapter.postProcess(c);
			})
			.logout(rememberLoginProvider::configureOAuth2Logout)
			.addFilterBefore(oAuth2RefreshFilter, AnonymousAuthenticationFilter.class);
		
		cookieRememberMeServices.install(http);
		
		final DefaultSecurityFilterChain build = http
			.with(new TotalVaadinFlowSecurityConfigurer(), Customizer.withDefaults())
			.build();
		
		LOG.info("Configuration finished - {} is spooled up and operational", this.getClass().getSimpleName());
		
		return build;
	}
}
