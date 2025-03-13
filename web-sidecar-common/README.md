# Common Web Sidecars

## What is a (web) sidecar?

A "web sidecar" is a service that is running next to the main/default/core application, that is not directly related to it.

Examples include:
* Static files (robots.txt, favicon, ...)
* WebServer error pages
* Spring Boot's Actuator (Metrics, Management, ...)

This module contains basic code to handle them.

> [!NOTE]
> Spring Boot Actuator specific code is located in a [dedicated module](../web-sidecar-actuator)

<details><summary>Detailed explanation</summary>

## Why is this required?

Request flow roughly that way:
```
-> Filters -> HandlerMappings -> Servlet (e.g. VaadinServlet)
```

The problem is that for static resources (when using a custom servlet - like Vaadin does) or the actuator endpoint, the default security mapping (OIDC) may not be applied (due to different auth) and therefore UNAUTHORIZED requests may enter the Servlet!<br/>
This can also result in unwanted session creation (depending on Session policy and servlet code)!

To fix this the following can be done:

#### 1. Actuator

Actuator handles Request on Filter level, however as not all exposed endpoints are known, ``/actuator/**`` is exposed.<br/>
If an endpoint does not exist (e.g. ``/actuator/abc``) the request will end up inside the underlying Servlet - which is not desired.<br/>
Therefore all Actuator request are blackholed immediately after the Filter using a custom handlerMapping.

Requests flow roughly like this:
```
-> ActuatorFilter(handles request)
-> ActuatorFilter -> BlackHoleHandlerMapping -> BlackHoleController
```

#### 2. Static-Resources

Static resources might be handled by the Servlet (as it replaces Spring Boots default behavior).
For example Vaadin has the following behavior (``VaadinServlet#serveStaticOrWebJarRequest``):
* (A) If a static resource was found: Serve it and exit
* (B) Otherwise go on, find/create a session, try show View etc

All resource should therefore matched exactly to their corresponding path on security level (A) as otherwise unwanted Vaadin-Sessions would occur (B).

</details>

## Summary of contents

### Public Stateless

This Security Provider intercepts specified requests and handles them like public stateless contents that are always accessible.

### Blackholing

> [!NOTE]
> This is usually not needed in simple Spring Boot applications that don't use custom servlets or similar technologies (e.g. Vaadin).<br/>
> However due to security considerations it's still active by default.<br/>
> You can disable it using ``sse.sidecar.black-holing.enabled=false``

This Security Provider intercepts specified requests and answers them with ``404 Not found``.

It's main use-case is to prevent the requests from reaching the main application, causing problems such as unwanted session creation.

> [!NOTE]
> ``favicon.ico`` is [blocked by default](./src/main/java/software/xdev/sse/web/sidecar/blackholing/FaviconBlackHolingPathsProvider.java).
> This can be [undone when configured](./src/main/java/software/xdev/sse/web/sidecar/blackholing/BlackHolingSecurity.java).

### Error page compatibility

Ensure that registered Web-Server error pages are accessible.
