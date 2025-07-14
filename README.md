[![Latest version](https://img.shields.io/maven-central/v/software.xdev.sse/bom?logo=apache%20maven)](https://mvnrepository.com/artifact/software.xdev.sse/bom)
[![Build](https://img.shields.io/github/actions/workflow/status/xdev-software/spring-security-extras/check-build.yml?branch=develop)](https://github.com/xdev-software/spring-security-extras/actions/workflows/check-build.yml?query=branch%3Adevelop)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=xdev-software_spring-security-extras&metric=alert_status)](https://sonarcloud.io/dashboard?id=xdev-software_spring-security-extras)

# Extras for Spring Security (SSE)

This framework provides various modules which make it easier and safer to create or manage various security solution in Spring apps, especially in distributed systems.<br/>
It adds features like remembering the user’s last identity provider, automatic logout checks, improved OAuth2/OIDC management, smoother frontend integration and remembering the login after a server restart.<br/>
It also secures system endpoints, avoids unnecessary sessions and comes with built-in metrics.

Nearly everything can be overridden with a custom implementation or disabled if required.

## Modules

Please note that more detailed descriptions are available in the individual modules.

* [bom](./bom/)
    * Bill of Materials for easier version management
* [oauth2-oidc](./oauth2-oidc/)
    * Revalidates the login periodically
    * Communicates logouts to the frontend
    * Makes it possible to automatically reselect the last login provider
* [oauth2-oidc-remember-me](./oauth2-oidc-remember-me/)
    * Stores, manages and encrypts OIDC login information safely in a distributed system
* [vaadin](./vaadin/)
    * Full Spring Security control before Vaadin handles requests
    * Creates Vaadin sessions only when needed
    * CSRF request whitelisting
    * Built-in Content Security Policy
* [web](./web/)
    * Stores the used login url
    * Determines if Cookies should be secured
* [web-sidecar-actuator](./web-sidecar-actuator/)
    * Secures Spring Boot's Actuator
    * Multi-User support
    * Allows securing different endpoint per user
    * Only password hashes are stored on the server side
* [web-sidecar-common](./web-sidecar-common/)
    * Host static resources without creating sessions
    * Prevent unwanted requests from reaching the underlying app/servlet
    * Ensures that error pages are accessible

## Usage
Some example use-cases (with integration tests) are available in the [demo](./demo).

Otherwise please have a look at the corresponding modules and their (Java) docs.

## Installation
[Installation guide for the latest release](https://github.com/xdev-software/spring-security-extras/releases/latest#Installation)

## Support
If you need support as soon as possible and you can't wait for any pull request, feel free to use [our support](https://xdev.software/en/services/support).

## Contributing
See the [contributing guide](./CONTRIBUTING.md) for detailed instructions on how to get started with our project.

## Dependencies and Licenses
View the [license of the current project](LICENSE) or the [summary including all dependencies](https://xdev-software.github.io/spring-security-extras)
