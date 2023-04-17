### Goals

1. Start Environment (Kafka + Observability)
2. Run `data-demo` and produce mock data
3. Run `stream-processing-workshop`
4. Explore distributed tracing data

### Start Kafka (Redpanda) Cluster

The data-demo project has a [kafka environment](https://github.com/schroedermatt/data-demo/blob/main/kafka/local/cluster/docker-compose-redpanda.yml) that can be started via Docker.

```bash
# run from the root of the data-demo repository
./gradlew kafkaComposeUp
```

Validate that Redpanda is up and running by navigating to [http://localhost:3000](http://localhost:3000)

### Start Observability Stack (Jaeger)

The data-demo project has an additional [observability environment](https://github.com/schroedermatt/data-demo/blob/main/observability/docker-compose.yml) that can be started via Docker. This includes Jaeger, a tracing backend and UI.

```bash
# run from the root of the data-demo repository
./gradlew tracingComposeUp

--

+----------+----------------+-----------------+
| Name     | Container Port | Mapping         |
+----------+----------------+-----------------+
| jaeger_1 | 16686          | localhost:16686 |
| jaeger_1 | 4317           | localhost:4317  |
| jaeger_1 | 4318           | localhost:4318  |
+----------+----------------+-----------------+
```

Validate that Jaeger is up and running by navigating to [http://localhost:16686](http://localhost:16686)

### Run data-demo with Tracing Enabled

> See this [Hands On](https://github.com/schroedermatt/data-demo/blob/main/assets/01_hands-on.md) for additional details on cloning, configuring, and running [data-demo](https://github.com/schroedermatt/data-demo).

In the [gradle.properties](https://github.com/schroedermatt/data-demo/blob/main/gradle.properties#L5) file, update the `tracingEnabled` flag to `true`.

```properties
tracingEnabled=true
```

Start the mockdata-api.

```
# run from the root of the data-demo repository
./gradlew bootRunApi
```

### Produce Mock Data

1. Generate a Customer (also creates an Address, Email, and Phone linked to the Customer)

```bash
# create customer
curl --request POST --url http://localhost:8080/customers

## command output ##

{ 
  "customer": { "id":"015965944", .. },
  "address": { .. },
  "email": { .. },
  "phone": { .. }
}
```

2. Generate an Artist

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

3. Stream an Artist (**Customer ID & Artist ID REQUIRED**)


**Use the Customer ID from step 1 and Artist ID from step 2 in the request body.**

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

#### View Results in Jaeger

Open Jaeger (http://localhost:16686/) and select the `data-demo` service before clicking "Find Traces".

Select a trace and view the results.

### Run "Top Customer Artists" with Tracing Enabled

>  See this [Hands On](https://github.com/schroedermatt/data-demo/blob/main/assets/02_hands-on.md) for additional details on cloning, configuring, and running [stream-processing-workshop](https://github.com/schroedermatt/stream-processing-workshop).

In the [gradle.properties](https://github.com/schroedermatt/stream-processing-workshop/blob/main/gradle.properties#L2) file, update the `tracingEnabled` flag to `true`.

```properties
tracingEnabled=true
```

Run the `TopCustomerArtists` stream.

```
# run from the root of the stream-processing-workshop
./gradlew -Pstream=org.improving.workshop.samples.TopCustomerArtists run
```

#### View Results in Jaeger

Open Jaeger (http://localhost:16686/) again and notice that TopCustomerArtists is now listed as a service. Select it before clicking "Find Traces".

Select a trace and view the results.

### Run "Customer Stream Count" with Tracing Enabled

> In the [gradle.properties](https://github.com/schroedermatt/stream-processing-workshop/blob/main/gradle.properties#L2) file, update the `tracingEnabled` flag to `true`.

```bash
# run from the root of the stream-processing-workshop
git checkout solutions

./gradlew -Pstream=org.improving.workshop.exercises.stateful.CustomerStreamCount run
```

#### View Results in Jaeger

Open Jaeger (http://localhost:16686/) again and notice that CustomerStreamCount is now listed as a service. Select it before clicking "Find Traces".

Select a trace and view the results.

### Cleanup

Tear down your environment when completed.

```bash
# run from the root of the data-demo repository

# tear down jaeger
./gradlew tracingComposeDown

# tear down kafka (redpanda)
./gradlew kafkaComposeDown
```
