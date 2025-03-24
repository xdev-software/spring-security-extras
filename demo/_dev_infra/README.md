## Development Infrastructure
The webapp requires e.g. a database to work.

The infrastructure contains the following:
* [MariaDB container](https://hub.docker.com/_/mariadb) that automatically gets the last test-system database dump from Jenkins
  * Exposed port: 3306
  * Driver for JDBC can be found at https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
* [Open ID Connect Mock Server](https://github.com/xdev-software/oidc-server-mock) - for login in
  * Available at http://localhost:4011

### Setup
* Requires Docker

#### Database
* Create ``db.env`` from [``db.env.template``](./db.env.template).
  * The file should not be tracked in Git - Check that
* Fill in the credentials (see the comments inside the file)

#### OIDC
* Create ``oidc-user-config.json`` from [``oidc-user-config.json.template``](./oidc-user-config.json.template)
  * File should not be tracked in Git
  * Fill in your login details like mail, name, password


### Usage
Note: Commands are all executed inside a shell/CMD in the current folder. ([Tip for windows users](https://stackoverflow.com/a/40146208))

| Use case | What to do? |
| --- | --- |
| Starting the infrastructure | ``docker compose up`` |
| Stopping (and removing) the infrastructure | ``docker compose down`` |
| (Re)Building the infrastructure<br/>e.g. after changes to the Dockerfiles | ``docker compose build --pull`` |

See also ``docker compose --help``

### Additional notes
⚠ The containers don't automatically restart after a PC restart!

⚠ After a PC restart the infrastructure is still present but it's stopped.<br/>
In this case you have 2 options:
* start the existing infrastructure again (``docker compose up``) or
* do a clean start by first removing (``docker compose down``) and then starting the infrastructure
