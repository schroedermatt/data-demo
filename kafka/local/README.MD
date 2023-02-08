# LOCAL KAFKA

This folder contains a local dockerized kafka environment.

## Start Environment

From the root directory, run `./gradlew kafkaComposeUp` to start all of the services found in `./cluster/docker-compose.yml`

## Redpanda Keeper `rpk`

https://docs.redpanda.com/docs/reference/rpk/

### Cluster Info

```bash
# view cluster info
> docker exec -it redpanda-0 rpk cluster info 

CLUSTER
=======
redpanda.458a54ba-fe7a-4d67-8546-f3299bf1a602

BROKERS
=======
ID    HOST        PORT
0*    redpanda-0  9092
1     redpanda-2  9092
2     redpanda-1  9092

TOPICS
======
NAME      PARTITIONS  REPLICAS
_schemas  1           1
```

### Create Topic, Produce, Consume

```bash
> docker exec -it redpanda-0 rpk topic create twitch_chat

> docker exec -it redpanda-0 rpk topic produce twitch_chat

> docker exec -it redpanda-0 rpk topic consume twitch_chat --num 1
```

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