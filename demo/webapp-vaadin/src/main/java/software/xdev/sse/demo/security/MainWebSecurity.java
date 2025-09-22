package software.xdev.sse.demo.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import software.xdev.spring.security.web.authentication.ui.advanced.AdvancedLoginPageAdapter;
import software.xdev.sse.csp.CSPGenerator;
import software.xdev.sse.demo.ui.structure.MainView;
import software.xdev.sse.oauth2.filter.OAuth2RefreshFilter;
import software.xdev.sse.oauth2.loginurl.OAuth2LoginUrlStoreAdapter;
import software.xdev.sse.oauth2.rememberloginproviderredirect.CookieBasedRememberRedirectOAuth2LoginProvider;
import software.xdev.sse.oauth2.rememberme.OAuth2CookieRememberMeServices;
import software.xdev.sse.vaadin.TotalVaadinFlowWebSecurity;


@EnableWebSecurity
@Configuration
public class MainWebSecurity extends TotalVaadinFlowWebSecurity
{
	private static final Logger LOG = LoggerFactory.getLogger(MainWebSecurity.class);
	
	@Autowired
	protected OAuth2CookieRememberMeServices cookieRememberMeServices;
	
	@Autowired
	protected OAuth2RefreshFilter oAuth2RefreshFilter;
	
	@Autowired
	protected CSPGenerator cspGenerator;
	
	@Autowired
	protected CookieBasedRememberRedirectOAuth2LoginProvider rememberLoginProvider;
	
	@Autowired
	protected OAuth2LoginUrlStoreAdapter oAuth2LoginUrlStoreAdapter;
	
	@Override
	protected void configure(final HttpSecurity http) throws Exception
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
				.contentSecurityPolicy(p -> p.policyDirectives(this.cspGenerator.buildCSP()))
				// https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options
				.contentTypeOptions(Customizer.withDefaults())
				.referrerPolicy(p -> p.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)))
			.oauth2Login(c -> {
				c.defaultSuccessUrl("/" + MainView.NAV);
				this.rememberLoginProvider.configureOAuth2Login(c);
				this.oAuth2LoginUrlStoreAdapter.postProcess(c);
			})
			.logout(this.rememberLoginProvider::configureOAuth2Logout)
			.addFilterBefore(this.oAuth2RefreshFilter, AnonymousAuthenticationFilter.class);
		
		this.cookieRememberMeServices.install(http);
		
		super.configure(http);
		
		LOG.info("Configuration finished - {} is spooled up and operational", this.getClass().getSimpleName());
	}
}
