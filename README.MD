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
./gradlew postgresComposeDownForce

# KAFKA -- only needed if running the connector
./gradlew kafkaComposeUp
./gradlew kafkaComposeDown
./gradlew kafkaComposeDownForce


# EVERYTHING -- helpful if running Postgres with Connector publishing to Kafka
./gradlew fullComposeUp
./gradlew fullComposeDown
./gradlew fullComposeDownForce
```

2. Run the mockdata-api ONLY

```bash
./gradlew :mockdata-api:bootRunMockData
```

3. Send a sample request

```bash
# remove jq if you don't have it installed, jq will just help format the response nicely in your terminal
curl -X POST localhost:8080/customers | jq
```

For the full request collection, import the [Insomnia Collection](./assets/DataDemo_Insomnia_2022-10-20.json).

---

Run the mockdata-api AND the mockdata-daemon -

```bash
# --parallel will start both at the same time
./gradlew bootRunMockData --parallel 

# you could also run the below commands from separate terminal tabs to have a cleaner separation of logs

## run from tab 1
./gradlew :mockdata-api:bootRunMockData
## run from tab 2
./gradlew :mockdata-daemon:bootRunMockData
```

## Observability - OTEL & Jaeger

The stack supports the OpenTelemetry and can be run with the Java Auto-Instrumentation turned on and exporting to Jaeger.

1) Start the observability stack - `./gradlew tracingComposeUp`
   - this will run the `docker-compose` file found in the `/observability` directory
2) Find the `bootRunMockData` task in the `build.gradle` file of either the api or daemon module depending on what you're running
3) Uncomment the `systemProperty` lines with "otel" properties & the `jvmArgs` line that attaches the otel java agent
4) View traces in Jaeger at `http://localhost:16686`

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