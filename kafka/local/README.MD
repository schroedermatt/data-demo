# LOCAL KAFKA

This folder contains a local dockerized kafka environment.

## Start Environment

From the root directory, run `./gradlew kafkaComposeUp` to start all of the services found in `./cluster/docker-compose.yml`

## Destroy Environment

Run `./gradlew kafkaComposeDown` to tear down the environment, including the local docker volumes.

## Additional Info

### Configure Source Connector

[Debezium Postgres Connector](https://debezium.io/documentation/reference/stable/install.html)

```bash
curl -X POST localhost:18083/connectors -H "Content-Type: application/json" -d @./kafka/local/connect/postgres-connector-config.json 
```

### List Kafka Topics

```bash
kafka-topics --list --bootstrap-server localhost:19092
```

### Consume Records from Topic

AVRO
```bash
kafka-avro-console-consumer \
   --topic topic-name-here \
   --from-beginning \
   --bootstrap-server localhost:19092 \
   --property schema.registry.url=https://localhost:8081
```

STRING
```bash
kafka-console-consumer \
   --topic topic-name-here \
   --bootstrap-server localhost:19092 \
   --property print.key=true \
   --from-beginning
```