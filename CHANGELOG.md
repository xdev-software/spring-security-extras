# 0.0.6

* Vaadin
    * Add Vaadin XHR subsystem
    * Fixes refresh loops with POST or similar requests
        * Fix wrong CSRF method being used, resulting in CSRF errors when these requests are encountered
        * DO NOT redirect these requests to login
* OAuth2/OIDC
    * Make it possible to specify applicable sources for ``OAuth2RefreshReloadCommunicator`` 

# 0.0.X

_Initial preview version for internal integration tests and release management checks_
