# Web Sidecar Actuator

Secures [Spring Boot's Actuator endpoints](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html).

For more details about sidecars please have a look at [``../web-sidecar-common``](../web-sidecar-common).

### Features
* Allows for multiple users
* Allows only specific endpoints per user
* Only the password hashes are stored on the server side
* The default built-in password-hasher is using `SHA-256`
* Utilizes password hash caching if possible
  * Enabled when one of the following libraries is detected on the class-path:
    * [caffeine](https://github.com/ben-manes/caffeine) 
    * [expiring-limited-cache](https://github.com/xdev-software/expiring-limited-cache)

Example configuration:
```yml
sse:
  actuator:
    users:
      # username = password
      # Hash is using SHA-256
      - username: actuator
        password-hash: 425edd11c26ae24d6726f66925c024ad7978400bd4ebb10bc943854ab93b3778
      - username: prometheus
        password-hash: 1809f7cd0c75acf34f56d8c19782b99c6b5fcd14128a3cc79aca38a4f94af3ff
        allowed-endpoints:
          - prometheus
```
