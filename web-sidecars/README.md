# Web Sidecars

A "web sidecar" is a service that is running next to the main/default/core application, that is not directly related to it.

Examples include:
* Static files (robots.txt, favicon, ...)
* WebServer error pages
* Spring Boot's Actuator (Metrics, Management, ...)

This module contains basic code to handle them.

> [!NOTE]
> Spring Boot Actuator specific code is located in a [different module](../web-sidecar-actuator)

## Why is this required?

Request flow roughly that way:
```
-> Filters -> HandlerMappings -> Servlet (e.g. VaadinServlet)
```

The problem is that for static resources (when using a custom servlet - like Vaadin does) or the actuator endpoint, the default security mapping (OIDC) may not be applied (due to different auth) and therefore UNAUTHORIZED requests enter the Servlet which should be prevented!<br/>
This also allows for unwanted session creation (depending on Session policy and servlet code)!

To fix this the following should done:

#### 1. Actuator

Actuator handles Request on Filter level, however as not all exposed endpoints are known, ``/actuator/**`` is exposed.<br/>
If an endpoint does not exist (e.g. ``/actuator/abc``) the request will end up inside the underlying Servlet - which is not desired.<br/>
Therefore all request are blackholed with a custom handlerMapping.

Requests flow roughly like this:
```
-> ActuatorFilter(handles request) 
-> ActuatorFilter -> BlackHoleHandlerMapping -> BlackHoleController
```

#### 2. Static-Resources

Static resources might be handled by the Servlet (as it replaces Spring Boots default behavior).
For example Vaadin has the following behavior (VaadinServlet#serveStaticOrWebJarRequest):
* (A) If a static resource was found serve it and exit
* (B) otherwise go on, find/create a session, try show View etc

All resource should therefore matched exactly to their corresponding path on security level (A) as otherwise unwanted VaadinSessions would occur (B).
