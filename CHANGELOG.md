# 2.0.0
_Minimum required Java version: 21_
* Updated to Spring Boot 4.x
  * Removed fork of `OidcUserService` to move closer to the standard implementation. Only fork the required `OidcUserRequestUtils#shouldRetrieveUserInfo`
* Update Vaadin to 25
  * Removed `TotalVaadinFlowWebSecurity` as it's no longer supported
  * Please note that Stylesheet [may now required being added to `PublicStatelessPathsProvider`](https://vaadin.com/docs/v25/upgrading#themes-and-styling)
* Updated Jackson Databind to v3
* `ActuatorUserConfig`: Remove deprecated `passwordSha256` - Use `passwordHash` instead
* Migrated demos and tests
* Code cleanup

# 1.5.3
_Last expected version for Spring Boot 3.x_
* Updated dependencies

# 1.5.2
* Improve default HSTS customization logic

# 1.5.0
* Vaadin
  * Made the way `HttpSecurity#securityMatcher` is applied in Sidecars customizable #221
    * By default `PathPatternRequestMatcher` is now used instead of `MvcRequestMatcher` or `AntPathRequestMatcher`
    * This fixes problems where the app/servlet utilizes an existing `urlMapping` that could result in incorrect path interception.
    * Further details can be found in `software.xdev.sse.web.sidecar.httpsecurity`
  * Fix incorrect lookup of `LoginUrlStore` in `TotalVaadinFlowSecurityConfigurer#configureLoginViewFromLoginUrlStore`
  * `TotalVaadinFlowSecurityConfigurer` make it possible to enable/disable certain configurations
* Web
  * Add customizer for [HSTS](https://en.wikipedia.org/wiki/HTTP_Strict_Transport_Security)
    * also used by sidecars (e.g. Actuator)
    * HSTS is disabled by default if SSL/HTTPS is also disabled
    * Further details can be found in `software.xdev.sse.web.hsts`
* Updated dependencies

# 1.4.0
* Vaadin
  * Deprecated `TotalVaadinFlowWebSecurity` because `VaadinWebSecurity` is deprecated
  * Added wrapper for Vaadin's new `TotalVaadinFlowSecurityConfigurer` #196
    * Updated the demo accordingly

# 1.3.1
* Actuator
  * Disabled caching by default
  * `SHA-256` is usually faster than utilizing a cache
    * This heavily depends on the used hardware, however most systems have hardware acceleration for `SHA-256`
  * Removed `expiring-limited-cache` cacher as it provides no advantage

# 1.3.0
* Actuator
  * Added support for custom password hashers
    * The default built-in password-hasher is still using `SHA-256`
  * `ActuatorUserConfig#passwordSha256` was renamed to `passwordHash`
    * `passwordSha256` is deprecated and will be removed in a future release
  * Now utilizes password hash caching if possible
    * The cache defaults to a maximum size of 100 and a cached duration of 1h
    * Enabled when one of the following libraries is detected on the class-path:
      * [caffeine](https://github.com/ben-manes/caffeine) 
      * [expiring-limited-cache](https://github.com/xdev-software/expiring-limited-cache)
    * Can be disabled with `sse.sidecar.actuator.password-hash.cache.enabled` if required
    * See source code for details configuration options
* Updated dependencies

# 1.2.2
* Minor code cleanup
* Updated dependencies

# 1.2.1
* Added more tests
* Updated dependencies

# 1.2.0
* Added ``LoginUrlStore``
    * Stores the login url so that it can be used inside other parts of applications to e.g. display dedicated login components
    * Enabled by default, can be disabled with ``sse.web.login-url-store.enabled=false``
* OAuth2-OIDC
    * Added ``OAuth2LoginUrlStoreAdapter`` to determine the login url
* Vaadin
    * Now handles ``LoginUrlStore`` if present and set's the value to ``NavigationAccessControl#setLoginView``
        * This is usually only needed when the authentication is anonymous and navigation to a view that requires non-anonymous authentication happens

# 1.1.0
* Updated to Spring Boot 3.5

# 1.0.5
* Migrated deployment to _Sonatype Maven Central Portal_ [#155](https://github.com/xdev-software/standard-maven-template/issues/155)
* Updated dependencies

# 1.0.4
* OAuth2-OIDC
    * ``DefaultDeAuthApplier``: Use already present request/response if possible

# 1.0.3
* Vaadin
    * Fix ``VaadinOAuth2RefreshReloadCommunicator`` not always setting status code ``401`` (which causes ``xhrAdapter.js`` to ignore the response)
        * This should only affect applications with anonymous auth enabled
* OAuth2-OIDC
    * Do not register ``OAuth2RefreshFilter`` twice
    * DeAuth JS-556
        * Apply correctly
        * Make it possible to customize application

# 1.0.2
* Vaadin
    * ``XHRReloadVaadinServiceInitListener`` #45
        * Improved performance by not building element every request and cloning it instead
        * If an error occurs while the script is added to the document the error is now logged (once at WARN; all subsequent ones at DEBUG)

# 1.0.1
* Vaadin
    * Fix ``SecureVaadinRequestCache`` ignoring non-optional url parameters

# 1.0.0
_Initial production ready release_

* Added demo + integration tests

# 0.0.7
* Vaadin
    * Fix XHR Reload not working (header missing) due to incorrect auto-configuration order

# 0.0.6
* Vaadin
    * Add Vaadin XHR Reload subsystem
    * Fixes Vaadin being stuck in loops with POST or similar requests when authentification expires
        * Fix wrong CSRF method being used, resulting in CSRF errors when these requests are encountered
        * DO NOT redirect these requests to login
* OAuth2/OIDC
    * Make it possible to specify applicable sources for ``OAuth2RefreshReloadCommunicator`` 

# 0.0.X

_Initial preview version for internal integration tests and release management checks_
