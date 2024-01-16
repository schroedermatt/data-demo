## Introduction

In today's hands on, we're going to validate that we can start the Dockerized Kafka Cluster & run the data generation app (data-demo) locally. This setup will be used throughout the semester for various activities as well as your project.

We will cover Kafka in depth in the upcoming weeks. Today is all about validating your local environment and ability to run the stack.

## Goals

1. Clone `data-demo`
2. Start Local Kafka (Confluent) Cluster
3. Run `data-demo` & produce mock data
4. Validate data was produced

## 1) git clone data-demo

Clone the [data-demo](https://github.com/schroedermatt/data-demo) repository - this project was built from the ground up for this course. Within the project, we've created a variety of environments that can be booted up via various Gradle tasks.

```bash
git clone https://github.com/schroedermatt/data-demo

cd data-demo
```

## 2) Start Local Kafka Cluster

1. Start Docker (if not already started)
2. Start [Kafka 1 Stack](https://github.com/schroedermatt/data-demo/blob/main/kafka/local/kafka-1/docker-compose.yml) - this is a simplified stack to ease the load on your machine
    - This leverages the [`docker-compose` Gradle plugin](https://github.com/avast/gradle-docker-compose-plugin) ([configuration](https://github.com/schroedermatt/data-demo/blob/main/build.gradle#L52-L57))

```bash
# run from root dir of data-demo

./gradlew kafka1ComposeUp

# wait for the following output in your terminal

# ... some output

# redis starts first, it is used by data-demo as cache
+-------+----------------+----------------+
| Name  | Container Port | Mapping        |
+-------+----------------+----------------+
| redis | 6379           | localhost:6379 |
+-------+----------------+----------------+

# ... more output

# kafka starts (single broker, connect worker, schema registry)
+------------------------+----------------+-----------------+
| Name                   | Container Port | Mapping         |
+------------------------+----------------+-----------------+
| kafka1_broker-1        | 19091          | localhost:19091 |
+------------------------+----------------+-----------------+
| kafka1_connect-1       | 8083           | localhost:18083 |
+------------------------+----------------+-----------------+
| kafka1_schema-registry | 8081           | localhost:8081  |
+------------------------+----------------+-----------------+
```

3. Validate Cluster Startup
    - View container status - `docker ps` (see below)

```bash
docker ps

## command output (first 2 columns shown) ##

CONTAINER ID   IMAGE  
25ca84ed77af   confluentinc/cp-kafka-connect:7.5.1
8616e0532f0f   confluentinc/cp-kafka:7.5.1
5e2078710584   confluentinc/cp-schema-registry:7.5.1
b6da33fb1854   redis:6.2-alpine
```

## 3) Run the data-demo

There are a couple options for running the data-demo app to generate mock data into your Kafka cluster. We're going to leverage the daemon today, which will dump a configurable amount of data into the Kafka cluster.

### data-demo Daemon

The data-demo Daemon is a process that will automatically generate the configured [initial load amounts](https://github.com/schroedermatt/data-demo/blob/main/gradle.properties#L8-L13) for each entity and then [on a schedule](https://github.com/schroedermatt/data-demo/blob/main/mockdata-daemon/src/main/java/org/msse/demo/kafka/KafkaDaemon.java#L38-L66) continue producing mock data.

1. Start the Daemon

```bash
# run from root dir of data-demo

./gradlew bootRunDaemon
```

> Note: The Daemon uses Redis to keep track of what has been created. If you start the Daemon a second time and it's already completed the initial load, it will not run an additional load. You can increase the initial load properties if you need to load more data.

## 4) Validate Data Produced

To simplify things, we can rely on our Docker containers to have the various CLIs that are offered by Kafka.

To demonstrate and validate that data was produced, we're going to run the command below to consume the Venues topic from the beginning.

We will dive much deeper into these commands and what is happening here during our Kafka week.

```bash
# run from root dir of data-demo

./kafka/local/kafka-1/bin/kafka-console-consumer --topic data-demo-venues --from-beginning

# *** output will display here ***

# CTRL + C to stop consumption

Processed a total of 18 messages
```