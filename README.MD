# DATA DEMO

The Data Demo [generates fake data](https://fakerjs.dev/) aligned with the ERD below.

The Data Demo can throw data into the following environments -

1) [Local Postgres (Docker)](#postgresql)
2) [Local Kafka (Docker)](#kafka)
3) [Local Postgres -> Kafka Connect -> Kafka (Docker)](#postgres--kafka)
4) [Confluent Cloud](#confluent-cloud) ([tf-ccloud](./tf-ccloud/README.md))

For more details on running the Data Demo, jump ahead to the [getting started section](#getting-started) after installing the [prerequisites](#environment-prerequisites).

<img src="./assets/ERD.png" alt="erd" width="750"/>

## Environment Prerequisites

* [git](PREREQS.MD#git)
* [Java 17](PREREQS.MD#java-17)
* [Docker](PREREQS.MD#docker)
* [IDE](PREREQS.MD#ide)
  * [SDK Setup](PREREQS.MD#project-sdk-configuration) & [Lombok Config](PREREQS.MD#enable-annotation-processors-for-lombok)
* [jq](PREREQS.MD#jq) (optional)

## Getting Started

1. In the [root gradle.properties](./gradle.properties) file, select your `runtimeMode` (brief descriptions below).
   * `postgres`: data-demo will inject mock data into a local postgres database.
   * `kafka`: data-demo will produce mock data into Kafka. A redis cache is used to keep track of the produced mock entities.
   * `kafka-cloud`: data-demo will connect to a Confluent Cloud cluster and produce mock data. A local redis cache is still used to keep track of the produced mock entities.
2. Start the [environment](#local-environments) for your selected `runtimeMode`
   * [PostgreSQL](#postgresql)
   * [Kafka](#kafka)
   * [Confluent Cloud](#confluent-cloud)
   * [Tracing](#tracing)
3. If you're enabling tracing, start the [tracing environment](#tracing) and set `tracingEnabled=true` in the [`gradle.properties`](./gradle.properties) file.
4. Configure the initial load volumes (these do not apply if running the `mockdata-api`). These properties are in the [`gradle.properties`](./gradle.properties) file.
   ```properties
   initialLoadCustomers=20
   initialLoadArtists=20
   initialLoadVenues=15
   initialLoadEvents=100
   initialLoadTickets=10000
   initialLoadStreams=50000
   ```
5. Run `data-demo`. There are 2 modes of operation, a daemon and an API. More details can be found on each below.
   1. `mockdata-daemon`, run via `./gradlew bootRunDaemon`
      * The daemon will hydrate the configured initial load volumes and then continue to run and inject random data into the environment.
   2. `mockdata-api`, run via `./gradlew bootRunAPI`
      * The API exposes endpoints to manually create entities. Import the [Insomnia Collection](./assets/DataDemo_Insomnia_2022-10-20.json) to explore the available endpoints.
        * To validate the API, you can fire off a sample request with this command -> `curl -X POST localhost:8080/customers | jq`

## Local Environments

The `docker-compose` gradle plugin is used to start the services in appropriate docker-compose files.

### PostgreSQL

1. Start [Postgres Services](./postgres/local/docker-compose.yml)

   ```bash
   # `docker-compose` gradle plugin is used to start services
   
   # POSTGRESQL
   ./gradlew postgresComposeUp
   ./gradlew postgresComposeDown
   ./gradlew postgresComposeDownForced
   ```

2. Validate Environment
   * pgAdmin4 (Postgres Exploration UI) available at `http://localhost:5433`
     * username: `root@email.com`
     * password: `root`
     * postgres database password: `postgres`

### Kafka

1. Start [Kafka Services](./kafka/local/cluster/docker-compose.yml)

```bash
# KAFKA
./gradlew kafkaComposeUp
./gradlew kafkaComposeDown
./gradlew kafkaComposeDownForced

```

2. Validate Environment 
   * KPow (Kafka Exploration UI) available at `http://localhost:3000`

### Postgres + Kafka

If you're planning to load data from Postgres into Kafka via Kafka Connect, run the following commands.

1. Start All Services ([Postgres](./postgres/local/docker-compose.yml) & [Kafka](./kafka/local/cluster/docker-compose.yml))

```bash
./gradlew fullComposeUp
./gradlew fullComposeDown
./gradlew fullComposeDownForced
```

2. Validate Environment
   * Postgres
     * pgAdmin4 (Postgres Exploration UI) available at `http://localhost:5433`
        * username: `root@email.com`
        * password: `root`
        * postgres database password: `postgres`
   * Kafka
     * KPow (Kafka Exploration UI) available at `http://localhost:3000`

### Confluent Cloud

The Confluent Cloud environment is provisioned via Terraform. You will need the Confluent CLI installed and logged in.

1) Provision Confluent Cloud Environment - see the [tf-ccloud readme](./tf-ccloud/README.md) for more details.
2) After the environment is provisioned, configure the bootstrap-servers, api-key, and api-secret
   ```bash
   terraform output resource-ids
   
   export CONFLUENT_CLOUD_BOOTSTRAP_SERVER="pkc-ymrq7.us-east-2.aws.confluent.cloud:9092"
   export CONFLUENT_CLOUD_API_KEY="***REDACTED***"
   export CONFLUENT_CLOUD_API_SECRET="***REDACTED***"
   ```

### Tracing

If you want to run the OpenTelemetry stack and enable distributed tracing, start up the [tracing services](./observability/docker-compose.yml) (Jaeger).

This command is run separate from the above commands that start postgres/kafka. The networking between the two docker compose files is configured to allow them to communicate.

   ```bash
   ./gradlew tracingComposeUp
   ./gradlew tracingComposeDown
   ./gradlew tracingComposeDownForced
   ```

2. Validate Environment
   * Jaeger (Tracing UI) available at `http://localhost:16686`
   
## Troubleshooting

### Invalid Java Version

The following error is likely the cause of not having Java 17 installed.

```bash
> error: invalid source release: 17
```

## Stream Processing

Ready to do something with this data? Go check out the [stream-processing-workshop](https://github.com/schroedermatt/stream-processing-workshop).