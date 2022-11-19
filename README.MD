# MSSE DATA DEMO

This project will start up a local environment and generate data of the following types.

- Customer
- Address
- Email
- Phone
- Artist
- Event
- Stream
- Ticket
- Venue

ERD can be found in the [mockdata readme.](./mockdata/README.MD)

## Environment Prerequisites

### IntelliJ CE

https://www.jetbrains.com/idea/download/#section=mac

_Feel free to use another IDE, but IntelliJ has become the industry defacto IDE._

### SDKMAN!

1) [Install SDKMAN!](https://sdkman.io/install)
2) Install Java 17 via SDKMAN!
    ```bash
    sdk install java 17.0.2-tem
    sdk use java 17.0.2-tem
    ```

### Set Project SDK

![project-sdk](./assets/project_sdk.png)

### Enable Annotation Processors for Lombok

![enable-annotation-processing](./assets/enable_annotation_processing.png)

_[Lombok Docs](https://projectlombok.org/)_

### Docker

https://docs.docker.com/get-docker/

### jq

https://stedolan.github.io/jq/download/

## Getting Started

1. Start ancillary services (PostgreSQL, Kafka, etc)

```bash
# `docker-compose` gradle plugin is used to start services

# POSTGRESQL
./gradlew postgresComposeUp
./gradlew postgresComposeDown
./gradlew postgresComposeDownForced

# pgAdmin4 (Postgres Exploration UI) available at `http://localhost:5433`
# pgAdmin4 username: root@email.com
# pgAdmin4 password: root
# pgAdmin4 postgres database password: postgres

# KAFKA
./gradlew kafkaComposeUp
./gradlew kafkaComposeDown
./gradlew kafkaComposeDownForced

# KPow (Kafka Exploration UI) available at `http://localhost:3000`

# EVERYTHING -- helpful if running Postgres with Connector publishing to Kafka
./gradlew fullComposeUp
./gradlew fullComposeDown
./gradlew fullComposeDownForced
```

--

If you want to run the OpenTelemetry stack and enable distributed tracing, also start up the tracing services (Jaeger)

```bash
./gradlew tracingComposeUp
./gradlew tracingComposeDown
./gradlew tracingComposeDownForced

# Jaeger UI available at `http://localhost:16686`
```

2. Validate Docker Startup
   1. Postgres:
      1. pgAdmin4 (Postgres Exploration UI) available at `http://localhost:5433`
         1. username: `root@email.com` 
         2. password: `root` 
         3. After logging in, it will prompt for postgres database password: `postgres`
            1. If it prompts for username, it's `postgres`.
   2. Kafka:
      1. KPow (Kafka Exploration UI) available at `http://localhost:3000`

3. Set `runtimeMode` and `tracingEnabled` depending on which ancillary services you started in step 1

These properties are located in the project's [root gradle.properties](./gradle.properties) file.

```properties
# options: postgres, kafka
runtimeMode=postgres

# options: true, false
tracingEnabled=false
```

3. Configure Initial Load Volume (if running the Daemon)

If you're running the Daemon, you can also configure the amount of data you would like to be initially loaded.

These properties are located in the project's [root gradle.properties](./gradle.properties) file.

```properties
initialLoadCustomers=100
initialLoadArtists=200
initialLoadVenues=50
initialLoadEvents=100
initialLoadTickets=1000
initialLoadStreams=1000
```

4. Run the API or Daemon

```bash
# runs the Mock Data API
./gradlew bootRunAPI

# runs the Mock Data Daemon
./gradlew bootRunDaemon
```

5. If running the API, send a sample request.

```bash
# remove jq if you don't have it installed, jq will just help format the response nicely in your terminal
curl -X POST localhost:8080/customers | jq
```

For the full request collection, import the [Insomnia Collection](./assets/DataDemo_Insomnia_2022-10-20.json).

## Observability - OTEL & Jaeger

The stack supports the OpenTelemetry and can be run with the Java Auto-Instrumentation turned on and exporting to Jaeger.

1) Start the observability stack - `./gradlew tracingComposeUp`
   - this will run the `docker-compose` file found in the `/observability` directory
2) Find the `bootRunMockData` task in the `build.gradle` file of either the api or daemon module depending on what you're running
3) Uncomment the `systemProperty` lines with "otel" properties & the `jvmArgs` line that attaches the otel java agent
4) View traces in Jaeger at `http://localhost:16686`

## Troubleshooting

### Invalid Java Version

The following error is likely the cause of not having Java 17 installed.

```bash
> error: invalid source release: 17
```

## Stream Processing Scenarios

The following scenarios can be accomplished using the data within this project.

1) Draw the topology.
2) Implement the topology.

### MOST STREAMED ARTISTS

Context: A com.msse.demo.customer streams (listens to) an artist.

Goals:
- Find the top 3 streamed artists across all customers.
- Find the top 3 streamed artists for each com.msse.demo.customer.
- BONUS) Group the customers that have the same top streamed artist.

### SOLD OUT EVENT

Context: Artists have events and customers book tickets to an event. Each event has a capacity (max tickets).

Goals:
- Alert when an event is within 10% of max capacity.
- Alert when an event is sold out.
- BONUS) Alert the event artist's top streamers when an event is created.

### MUST SEE VENUE

Context: Artists have events at a venue.

Goals:
- Find the venue that has hosted the most events.
- Find the venue that has hosted the most customers.

### SUGGESTED EVENTS - GENRE

Context: Customers stream artists and each artist has a genre.

Goals:
- Alert com.msse.demo.customer when an event is created in their most streamed genre.
- BONUS) Alert com.msse.demo.customer when popular* events are created in their most streamed genre.
   - *popular: within 10% max capacity

### SUGGESTED EVENTS - LOCATION

Context: Customers have an address that indicates their location.

Goals:
- Find all tickets purchased by customers in each zipcode.
- BONUS) Alert com.msse.demo.customer when customers in their zipcode are buying tickets to a popular* event. 
   - *popular: within 10% max capacity