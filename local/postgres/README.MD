# LOCAL POSTGRES

This folder contains a local dockerized postgres environment.

## Start Environment

Run `./up.sh`, and you'll have -
* `postgres`: 14.1-alpine version of the databse
  * connection: `localhost:5432`
  * username: `postgres`
  * password: `postgres`
* `pgadmin4`: administration tool for `postgres`
  * connection: open browser and navigate to `localhost:5433`
  * username/email: `root@email.com`
  * password: `root`

`pgadmin4` may prompt you for password on the postgres database, the database password is `postgres`

## Destroy Environment

Run `./down.sh` to tear down the environment, including the local docker volumes.

## Additional Info

If you're struggling to find the postgres tables in `pgadmin`, the screenshot below illustrates the path to the tables in the sidebar.

![pgadmin](./assets/pgadmin.png)