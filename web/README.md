# Web

Common/Shared code for Spring Boot Web applications.

## Contents

### Cookie Safety

Stores / Tracks if cookies should contain the [``secure``](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Headers/Set-Cookie#secure) flag.

Default implementation: Uses the value from ``server.servlet.session.cookie.secure``.

### Login Url Store

Stores the login url if set.

Defaults to ``/login``.

#### Usage example

```java
public MainLayout {
  @Autowired
  protected LoginUrlStore loginUrlStore;

  void initUI() {
    Anchor anchorLogin = new Anchor(
          this.loginUrlStore.getLoginUrl(),
          new Button("Login"));
    // ...
  }
}
```

### HSTS

Configures HSTS, automatically picked up by sidecars for configuration.

Spring Boot has HSTS <a href="https://docs.spring.io/spring-security/reference/features/exploits/headers.html#headers-hsts">enabled by default</a> which means that it always checks if a request is secure or not. If the request is determined to be secure it injects an HSTS header. This is unnecessary as HSTS is nearly always handled by the reverse proxy upstream that also handles certificates.

The default implementation therefore disables HSTS when
* it was explicitly disabled in the config
* no SSL configuration is present
