# Security for OAuth2/OIDC

Secures Spring Boot's OAuth2/OIDC.

> [!NOTE]
> Primarily designed for OIDC (OAuth2 should also work but might require some manual fixes and adjustment) and E-Mails

## Contents

### CheckAuth

> [!NOTE]
> **Fixed the underlying problematic:**<br/>
> By default Spring only logs the user in. Once logged in there is no re-check if the user is still valid at the OAuth2/OIDC server.<br/>
> As long as the app is not restarted the user can potentially be logged in forever.

Used for checking if the used OAuth2/OIDC token is still valid.

Once the token is no longer valid it's recheck.

Also provides a [OAuth2/OIDC server "is offline" fallback](./src/main/java/software/xdev/sse/oauth2/checkauth/OAuth2ProviderOfflineManager.java), which means that the user is still considered valid when the server can't be reached for some time (default: 3h).

#### Requirements

* The OAuth2/OIDC Server should provide a [refresh token](https://datatracker.ietf.org/doc/html/rfc6749#section-1.5) (to recheck authentication).<br/>Usually this can be achieved with the [``offline_access`` scope](https://openid.net/specs/openid-connect-core-1_0.html#OfflineAccess) but it's highly dependent on the OIDC Provider. 
For example IdentityServer requires it, while KeyCloak must not use it at all (as the UI otherwise asks for a login every time) and requires a different setting.

#### Usage

* **You need to implement [``EmailBasedOAuth2AuthCheckerUserService``](./src/main/java/software/xdev/sse/oauth2/checkauth/EmailBasedOAuth2AuthCheckerUserService.java)** or provide a custom [``OAuth2IsDisabledChecker``](./src/main/java/software/xdev/sse/oauth2/checkauth/disabledcheck/OAuth2IsDisabledChecker.java).

### Refresh Filter

Enforces that requests are properly authenticated as specified in CheckAuth above.

#### Usage
* Inside your main ``WebSecurity#configure`` add:
    ```java
    http.addFilterBefore(this.oAuth2RefreshFilter, AnonymousAuthenticationFilter.class)
    ```

#### Reload Communication

Allows custom [``ReloadCommunicators``](./src/main/java/software/xdev/sse/oauth2/filter/reloadcom/OAuth2RefreshReloadCommunicator.java) to communicate to the client that the user is no longer authenticated and some kind of action (e.g. a page reload) is required.

### RememberMe Login Provider Redirect

Tries to auto auth the user using the last OAuth2 provider that was used by them.

#### Usage
* Inside your main ``WebSecurity#configure`` add:
    ```java
    http
        .oauth2Login(c -> {
            // Other stuff
            this.rememberLoginProvider.configureOAuth2Login(c);
        })
        .logout(this.rememberLoginProvider::configureOAuth2Logout)
    ```
