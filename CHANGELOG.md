# 1.0.3
* Vaadin
    * Fix ``VaadinOAuth2RefreshReloadCommunicator`` not always setting status code ``401`` (which causes ``xhrAdapter.js`` to ignore the response)
        * This should only affect applications with anonymous auth enabled
* OAuth2-OIDC
    * Do not register ``OAuth2RefreshFilter`` twice
    * DeAuth
        * Apply correctly
        * Make it possible to customize application

# 1.0.2
* Vaadin
    * ``XHRReloadVaadinServiceInitListener``
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
