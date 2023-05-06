# Hands On: Introduction to Kafka

> NOTE: `git pull` on data-demo's `main` branch before continuing!

## Goals

1. Clone data-demo
2. Start Local Kafka (Redpanda) Cluster
3. Create Topic, Produce, Consume
4. Run data-demo & produce mock data

## Getting Started
### Start Local Kafka (Redpanda) Cluster

1. Clone the [data-demo](https://github.com/schroedermatt/data-demo) repository

```bash
git clone https://github.com/schroedermatt/data-demo

cd data-demo
```

2. Start Docker (if not already started)
3. Start [Kafka Stack](https://github.com/schroedermatt/data-demo/blob/main/kafka/local/cluster/docker-compose-redpanda.yml)
    - This leverages the [`docker-compose` Gradle plugin](https://github.com/avast/gradle-docker-compose-plugin) ([configuration](https://github.com/schroedermatt/data-demo/blob/main/build.gradle#L47-L53))

```bash
# run from root dir of data-demo

./gradlew redpandaComposeUp

# wait for the following output in your terminal

+------------------+----------------+-----------------+
| Name             | Container Port | Mapping         |
+------------------+----------------+-----------------+
| redpanda-0       | 18081          | localhost:18081 |
| redpanda-0       | 18082          | localhost:18082 |
| redpanda-0       | 19092          | localhost:19092 |
| redpanda-0       | 9644           | localhost:19644 |
+------------------+----------------+-----------------+
| redpanda-2       | 38081          | localhost:38081 |
| redpanda-2       | 38082          | localhost:38082 |
| redpanda-2       | 39092          | localhost:39092 |
| redpanda-2       | 9644           | localhost:39644 |
+------------------+----------------+-----------------+
| redpanda-console | 8080           | localhost:3000  |
+------------------+----------------+-----------------+
| redpanda-1       | 28081          | localhost:28081 |
| redpanda-1       | 28082          | localhost:28082 |
| redpanda-1       | 29092          | localhost:29092 |
| redpanda-1       | 9644           | localhost:29644 |
+------------------+----------------+-----------------+
| redis            | 6379           | localhost:6379  |
+------------------+----------------+-----------------+
```

4. Validate Cluster Startup
    - Navigate to Redpanda Console at [http://localhost:3000](http://localhost:3000)
    - View Cluster Info (command below)

```bash
docker exec -it redpanda-0 rpk cluster info

## command output ##

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

### Produce / Consume Records

Once you've started your cluster (see previous section).

1. Create a Kafka Topic
    - [`rpk topic create`](https://docs.redpanda.com/docs/reference/rpk/rpk-topic/rpk-topic-create/)

```bash
docker exec -it redpanda-0 rpk topic create test.topic

## command output ##

TOPIC       STATUS
test.topic  OK
```

2. Produce Events
    - [`rpk topic produce`](https://docs.redpanda.com/docs/reference/rpk/rpk-topic/rpk-topic-produce/)

```bash
docker exec -it redpanda-0 rpk topic produce test.topic

## enter events as strings and hit return ##

event-1
Produced to partition 0 at offset 0 with timestamp 1679759132247.
```

3. Consume Events
    - [`rpk topic consume`](https://docs.redpanda.com/docs/reference/rpk/rpk-topic/rpk-topic-consume/)

```bash
docker exec -it redpanda-0 rpk topic consume test.topic --num 1

## command output ##

{
  "topic": "test.topic",
  "value": "event-1",
  "timestamp": 1679759132247,
  "partition": 0,
  "offset": 0
}
```

4. Explore Redpanda Console (http://localhost:3000/topics)

### Run the data-demo

There are two options for running the data-demo app to generate mock data into your Kafka cluster.

#### Option 1: data-demo API

The data-demo API is a RESTful API exposing the ability to generate mock data on demand.

1. Start the [data-demo API](https://github.com/schroedermatt/data-demo/blob/main/mockdata-api/src/main/java/org/msse/demo/api/MockDataApi.java)

```bash
./gradlew bootRunApi
```

2. Create Customer (also creates an Address, Email, and Phone linked to the Customer)

```bash
# create customer
curl --request POST --url http://localhost:8080/customers

## command output ##

{ 
	"customer": { "id":"015965944", .. },
	"address": { "id":"526050119", "customerid":"015965944", .. },
	"email": { "id":"322363290", "customerid":"015965944", .. },
	"phone": { "id":"724919575","customerid":"015965944", .. }
}
```

3. Create Artist

```bash
# create artist
curl --request POST --url http://localhost:8080/artists

## command output ##

{
  "id": "825378672",
  "name": "Munch",
  "genre": "Pop"
}
```

4. Create Venue (will link venue to existing Address or create a new one if none exist)

```bash
# create venue (tied to an address)
curl --request POST --url http://localhost:8080/venues

## command output ##

{
  "id": "290642571",
  "addressid": "526050119",
  "name": "Lowe-Morar Pavilion",
  "maxcapacity": 1337
}
```

5. Create Event (**Artist ID & Venue ID REQUIRED**)

```bash
# create an event for an artist at a venue
curl --request POST \
  --url http://localhost:8080/events \
  --header 'Content-Type: application/json' \
  --data '{
	"artistid": "825378672",
	"venueid": "290642571"
  }'

## command output ##

{
  "id": "763125137",
  "artistid": "825378672",
  "venueid": "290642571",
  "capacity": 495,
  "eventdate": "2023-05-06 02:13:18.996"
}
```

6. Book Ticket (**Customer ID & Event ID REQUIRED**)

```bash
curl --request POST \
  --url http://localhost:8080/tickets \
  --header 'Content-Type: application/json' \
  --data '{
	"customerid": "015965944",
	"eventid": "763125137"
  }'

## command output ##

{
  "id": "754250592",
  "customerid": "015965944",
  "eventid": "763125137",
  "price": 2217
}
```

7. Stream Artist (**Customer ID & Artist ID REQUIRED**)

```bash
curl --request POST \
  --url http://localhost:8080/streams \
  --header 'Content-Type: application/json' \
  --data '{
	"customerid": "015965944",
	"artistid": "825378672"
  }'

## command output ##

{
  "id": "324373339",
  "customerid": "015965944",
  "artistid": "825378672",
  "streamtime": "2023-03-25T16:11:38.692121Z"
}
```

After the above exercise, you should see nine "data-demo-" topics in [Redpanda Console](http://localhost:3000/topics) - you can also view the topics via [`rpk topic list`](https://docs.redpanda.com/docs/reference/rpk/rpk-topic/rpk-topic-list/).

```bash
docker exec -it redpanda-0 rpk topic list

## command output ##

NAME                 PARTITIONS  REPLICAS
_schemas             1           3
data-demo-addresses  1           1
data-demo-artists    1           1
data-demo-customers  1           1
data-demo-emails     1           1
data-demo-events     1           1
data-demo-phones     1           1
data-demo-streams    1           1
data-demo-tickets    1           1
data-demo-venues     1           1
test.topic           1           1
```

#### Option 2: data-demo Daemon

The data-demo Daemon is a process that will automatically generate the configured [initial load amounts](https://github.com/schroedermatt/data-demo/blob/main/gradle.properties#L8-L13) for each entity and then [on a schedule](https://github.com/schroedermatt/data-demo/blob/main/mockdata-daemon/src/main/java/org/msse/demo/kafka/KafkaDaemon.java#L38-L66) continue producing mock data.

1. Start the Daemon

```bash
./gradlew bootRunDaemon
```

> Note: The Daemon uses Redis to keep track of what has been created. If you start the Daemon a second time and it's already completed the initial load, it will not run an additional load. You can increase the initial load properties if you need to load more data.