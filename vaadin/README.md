# Vaadin

Secures [Vaadin (Flow)](https://github.com/vaadin/platform).

## Improvements

The overall goal is to
* give Spring Security full access control before any requests get's to Vaadin
* only create Vaadin Sessions when really needed as these are rather heavy (Vaadin stores the state of the UI in these)
* make Vaadin's ``VaadinWebSecurity`` better customizable

## Requirements

* ``com.vaadin:vaadin-spring`` must be provided manually (only included with scope ``provided`` by default to prevent versioning conflicts)

## Usage

Create a ``Configuration``-class that extends from ``TotalVaadinFlowWebSecurity`` and extend it accordingly.

Here is an example:
```java
@EnableWebSecurity
@Configuration
public class MainWebSecurity extends TotalVaadinFlowWebSecurity
{
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
            .headers(c -> c
                .contentSecurityPolicy(p -> p.policyDirectives(this.cspGenerator.buildCSP()))
                // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Content-Type-Options
                .contentTypeOptions(Customizer.withDefaults())
                .referrerPolicy(p -> p.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)))
            .sessionManagement(c -> c.sessionConcurrency(sc -> sc.maximumSessions(5)))
            .oauth2Login(c -> {
                c.defaultSuccessUrl("/" + WorkdayView.NAV);
                this.rememberLoginProvider.configureOAuth2Login(c);
                this.oAuth2LoginUrlStoreAdapter.postProcess(c);
            })
            .logout(this.rememberLoginProvider::configureOAuth2Logout)
            .addFilterBefore(this.oAuth2RefreshFilter, AnonymousAuthenticationFilter.class);
        
        this.cookieRememberMeServices.install(http);
        
        super.configure(http);
    }
}
```

## Other automatically on-demand applied modules

### CSP

Contains a pre-defined [Content Security Policy](../csp/) for Vaadin.

### CSRF

Whitelists CSRF requests that should not be processed by Vaadin.

### XHR Reload

Forces a page reload (for XHR requests) when the authentication expires (401).
