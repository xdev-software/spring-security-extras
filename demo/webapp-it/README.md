This module contains the integrations test for the WebApp using Selenium.

## Design
The project contains the following major packages:
* [cases](./src/test/java/software/xdev/tci/demo/webapp/cases/) contains the testcases
* [datageneration](./src/test/java/software/xdev/tci/demo/webapp/datageneration/) contains project specific datageneration logic
* [base](./src/test/java/software/xdev/tci/demo/webapp/base/) contains the basics that the testcases have in common. This includes:
  * ``BaseTest``: Contains the logic how the app server and it's underlying infrastructure is started, how videos are recorded and so on (most of the TCI features). Every test extends from this class
  * ``IntegrationTestDefaults`` (implemented by ``BaseTest``): An interface with common workflows that every test needs. For example: login, logout, waitUntil, ...
  * ``InfraPerCase/ClassTest``: Lifecycle specific implementations of ``BaseTest`` - either for starting all infrastructure per case or once per class
