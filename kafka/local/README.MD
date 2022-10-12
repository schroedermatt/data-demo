# LOCAL KAFKA

This folder contains a local dockerized kafka environment.

## Start Environment

Run `./up.sh`, and you'll have -

## Destroy Environment

Run `./down.sh` to tear down the environment, including the local docker volumes.

## Additional Info

### Configure Source Connector

[Debezium Postgres Connector](https://debezium.io/documentation/reference/stable/install.html)

```bash
curl -X POST localhost:18083/connectors -H "Content-Type: application/json" -d @./connect/connectors/postgres.json 
```

### List Kafka Topics

`kafka-topics --list --bootstrap-server localhost:19092`

### Consume Records from Topic

```bash
kafka-console-consumer \
   --topic _postgres_.public.customer \
   --bootstrap-server localhost:19092 \
   --property print.key=true \
   --from-beginning
```