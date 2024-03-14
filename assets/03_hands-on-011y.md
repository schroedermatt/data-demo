# Hands On: Observability

> NOTE: `git pull` on data-demo's `main` branch before continuing!

## Goals

1. Start Environment (Kafka + **Observability**)
2. Run `data-demo` and produce mock data
3. Run `stream-processing-workshop`
4. Explore distributed tracing data

## 1) Start Local Kafka Cluster

1. Start Docker (if not already started)
2. Start [Kafka 1 Stack](https://github.com/schroedermatt/data-demo/blob/main/kafka/local/kafka-1/docker-compose.yml) - this is a simplified stack to ease the load on your machine
    - This leverages the [`docker-compose` Gradle plugin](https://github.com/avast/gradle-docker-compose-plugin) ([configuration](https://github.com/schroedermatt/data-demo/blob/main/build.gradle#L52-L57))

```bash  
# run from root dir of data-demo    
./gradlew kafka1ComposeUp  
```  

3. Validate Cluster Startup with `docker ps`

```bash  
docker ps  

## command output (first 2 columns shown) ##    
CONTAINER ID   IMAGE  
25ca84ed77af   confluentinc/cp-kafka-connect:7.5.1
8616e0532f0f   confluentinc/cp-kafka:7.5.1
5e2078710584   confluentinc/cp-schema-registry:7.5.1
b6da33fb1854   redis:6.2-alpine
```

## 2) Start Observability Stack (Jaeger)

The data-demo project has an additional [observability environment](https://github.com/schroedermatt/data-demo/blob/main/observability/jaeger/docker-compose.yml) that can be started via Docker. This includes Jaeger, a simple tracing backend and UI.

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

In the search panel on the left, you may see a "jaeger-query" service listed but you should not see anything else at this point.

## 3) Run data-demo with Tracing Enabled

> See this [Hands On: Environment Setup](https://github.com/schroedermatt/data-demo/blob/main/assets/00_hands-on-setup.md) for additional details on cloning, configuring, and running [data-demo](https://github.com/schroedermatt/data-demo).

In the [gradle.properties](https://github.com/schroedermatt/data-demo/blob/main/gradle.properties#L5) file of `data-demo`, update the `tracingEnabled` flag to `true`. [When true](https://github.com/schroedermatt/data-demo/blob/main/mockdata-api/build.gradle#L36-L45), the `data-demo` will start up with the otel java agent attached to enable tracing.

```properties
tracingEnabled=true
```

Start the mockdata-api (not the daemon).

```
# run from the root of the data-demo repository
./gradlew bootRunApi
```

## 4) Produce Mock Data

Open another tab in your terminal to execute the following `curl` statements. These will call the running API and generate data.

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

3. Stream Artist (**Customer ID & Artist ID REQUIRED**)

> Use the Customer ID from step 1 and Artist ID from step 2 in the request body.

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

## 5) View Results in Jaeger

Open Jaeger [http://localhost:16686/](http://localhost:16686/) and select the `data-demo` service before clicking "Find Traces"

<img src="./images/jaeger-services.png" alt="jaeger" width="300"/>

Select one of the "data-demo: POST" traces and view the results. There should be a trace for each of the API requests you made in the previous section.

![jaeger](./images/jaeger-trace-1.png)

This trace shows the event being generated and produced to Kafka.

## 6) View `traceparent` in Message Headers

Curious to see how the trace is being propogated between the services? The `kafka-console-consumer` has a property to print record headers.

Let's consume the `data-demo-customers` topic and see what's hiding in the headers now that tracing is enabled.

```bash
./kafka-bin/kafka-console-consumer \
  --topic data-demo-customers \
  --group o11y-hands-on \
  --from-beginning \
  --property print.headers=true
  
--

traceparent:00-68a2c76609a012552ebec298cd9821b7-6c5a00b30338bb49-01	{"id":"380873384","type":"PREMIUM","gender":"U","fname":"Lane","mname":"Ewa","lname":"MacGyver","fullname":"Lane Ewa MacGyver","suffix":"DDS","title":"Customer Program Technician","birthdt":"1962-03-24","joindt":"2017-07-15"}
```

We've told the consumer to print out the headers and that's what we're seeing before the JSON object begins. There is a single header.

Ex -> `traceparent:00-68a2c76609a012552ebec298cd9821b7-6c5a00b30338bb49-01`
* Header Key: `traceparent` 
* Header Value: `00-68a2c76609a012552ebec298cd9821b7-6c5a00b30338bb49-01`

There is a lot jammed into the `traceparent` header value that is used (and propagated) by the OpenTelemetry instrumentation agents. An overview of the segments inside the traceparent is shown in the diagram below.

![traceparent](./images/traceparent.png)

When you run a Stream or other client that processes a message, it will now be able to grab this `traceparent` header and propagate (report) the trace to Jaeger to continue building a picture of the end-to-end flow of the message.

## 7) Run "Top Customer Artists" with Tracing Enabled

>  See this [Hands On](https://github.com/schroedermatt/data-demo/blob/main/assets/02_hands-on-stream-processing.md) for additional details on cloning, configuring, and running [stream-processing-workshop](https://github.com/schroedermatt/stream-processing-workshop).

Just like we did in `data-demo`, we now need to enable tracing **in the stream-processing-workshop** repository.

In the [gradle.properties](https://github.com/schroedermatt/stream-processing-workshop/blob/main/gradle.properties#L2) file of the **stream-processing-workshop project**, update the `tracingEnabled` flag to `true`.  [When true](https://github.com/schroedermatt/stream-processing-workshop/blob/main/build.gradle#L27-L36), the Stream(s) will start up with the otel java agent attached.

```properties
tracingEnabled=true
```

Run the `TopCustomerArtists` stream.

```
# run from the root of the stream-processing-workshop
./gradlew -Pstream=org.improving.workshop.samples.TopCustomerArtists run 
```

## 8) View Results in Jaeger

Open Jaeger [http://localhost:16686/](http://localhost:16686/) again and notice that `TopCustomerArtists` is now listed as a service. Select it before clicking "Find Traces".

Select a trace and view the results. Notice that the trace now extends from the origin (data-demo) all the way into the consuming service and you can see the full path. **COOL!**

![jaeger](./images/jaeger-trace-2.png)

## 9) Run "Customer Stream Count" with Tracing Enabled

Let's take it one step further to see what happens when **multiple consumers/streams** process the same event. The Customer Stream Count can be found on the `solutions` branch so you'll first need to checkout that branch before running the Stream.

> Before continuing, double check that the `tracingEnabled` flag is `true` in [gradle.properties](https://github.com/schroedermatt/stream-processing-workshop/blob/main/gradle.properties#L2)

```bash
# run from the root of the stream-processing-workshop
git checkout solutions

./gradlew -Pstream=org.improving.workshop.exercises.stateful.CustomerStreamCount run
```

## 10) View Results in Jaeger

Open Jaeger [http://localhost:16686/](http://localhost:16686/) again and notice that CustomerStreamCount is now listed as a service. Select it before clicking "Find Traces".

Select a trace and view the results. Notice that the trace now extends from the origin (data-demo) all the way into MULTIPLE consuming services. **SWEET!**

![jaeger](./images/jaeger-trace-3.png)

## 11) Cleanup

Now that your endorphins are flowing, it's time to tear down the environment (if you're done playing around).

```bash
# run from the root of the data-demo repository

# tear down jaeger
./gradlew tracingComposeDown

# tear down kafka
./gradlew kafka1ComposeDown
```