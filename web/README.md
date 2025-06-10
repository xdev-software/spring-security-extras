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
