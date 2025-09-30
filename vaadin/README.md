# Vaadin

Secures [Vaadin (Flow)](https://github.com/vaadin/platform).

## Improvements

The overall goal is to
* give Spring Security full access control before any requests are processed by Vaadin
* only create Vaadin Sessions when they are really needed - as these are rather heavy (Vaadin stores the state of the UI in these)
* make Vaadin's ``VaadinWebSecurity``/``VaadinSecurityConfigurer`` better customizable

## Requirements

* ``com.vaadin:vaadin-spring`` must be provided manually (only included with scope ``provided`` by default to prevent versioning conflicts)

## Usage

```java
@EnableWebSecurity
@Configuration
public class MainWebSecurity
{
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
            .headers(c -> c
                .contentSecurityPolicy(p -> p.policyDirectives(cspGenerator.buildCSP()))
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
        
        return http
            .with(new TotalVaadinFlowSecurityConfigurer(), Customizer.withDefaults())
            .build();
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
