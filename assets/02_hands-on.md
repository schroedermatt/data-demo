# Hands On: Stream Processing

## Goals

1. Clone `stream-processing-workshop`
2. Run `data-demo` & produce mock data
3. Run "Top Customer Artists" Stream
4. Solve Stateless Stream Processing Exercises
5. Solve Stateful Stream Procsesing Exercises (optional)

### Clone Stream Processing Workshop

```bash
git clone https://github.com/schroedermatt/stream-processing-workshop.git
```

Validate your project setup by running the [`TopCustomerArtistsSpec`](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/test/groovy/org/improving/workshop/samples/TopCustomerArtistsSpec.groovy).  You can run this via your Terminal or IDE.

```bash
 ./gradlew cleanTest test --tests TopCustomerArtistsSpec
```

### Run data-demo Daemon

> See the [Hands On](https://github.com/schroedermatt/data-demo/blob/main/assets/01_hands-on-confluent.md) from last week for additional details on cloning, configuring, and running [data-demo](https://github.com/schroedermatt/data-demo).

If you haven't already, start up Kafka and begin producing data with the data-demo daemon.

```bash
# start kafka cluster
./gradlew kafkaComposeUp

# start data-demo daemon
./gradlew bootRunDaemon
```

### Run "Top Customer Artists" Stream

Once data is being loaded into your local Kafka cluster, start up the [`TopCustomerArtists`](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/samples/TopCustomerArtists.java) Kafka Streams application. The stream will produce data to a topic named `"kafka-workshop-top-10-stream-count"`. Open up [Control Center](http://localhost:9021) and watch the data flow in as events are produced.

You can run the stream directly in your terminal or IDE (IntelliJ shown below).

```bash
./gradlew -Pstream=org.improving.workshop.samples.TopCustomerArtists run 
```

#### Topology Visualizer

When the TopCustomerArtists stream starts up, the internal Kafka Streams topology is [logged out](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/Streams.java#L104). The topology for this stream is below. This can be helpful in debugging or fact checking your work and making sure the topology looks how you would expect it to look. However, you may notice that the topology isn't super human-readable. There is an open source tool - https://zz85.github.io/kafka-streams-viz/ - that you can paste the topology in to visualize and inspect it.

Take the topology below and paste it into https://zz85.github.io/kafka-streams-viz/

```
Topologies:
   Sub-topology: 0
    Source: KSTREAM-SOURCE-0000000000 (topics: [data-demo-streams])
      --> KSTREAM-PEEK-0000000001
    Processor: KSTREAM-PEEK-0000000001 (stores: [])
      --> KSTREAM-KEY-SELECT-0000000002
      <-- KSTREAM-SOURCE-0000000000
    Processor: KSTREAM-KEY-SELECT-0000000002 (stores: [])
      --> customer-artist-stream-counts-repartition-filter
      <-- KSTREAM-PEEK-0000000001
    Processor: customer-artist-stream-counts-repartition-filter (stores: [])
      --> customer-artist-stream-counts-repartition-sink
      <-- KSTREAM-KEY-SELECT-0000000002
    Sink: customer-artist-stream-counts-repartition-sink (topic: customer-artist-stream-counts-repartition)
      <-- customer-artist-stream-counts-repartition-filter

  Sub-topology: 1
    Source: customer-artist-stream-counts-repartition-source (topics: [customer-artist-stream-counts-repartition])
      --> KSTREAM-AGGREGATE-0000000003
    Processor: KSTREAM-AGGREGATE-0000000003 (stores: [customer-artist-stream-counts])
      --> KTABLE-TOSTREAM-0000000007
      <-- customer-artist-stream-counts-repartition-source
    Processor: KTABLE-TOSTREAM-0000000007 (stores: [])
      --> KSTREAM-MAPVALUES-0000000008
      <-- KSTREAM-AGGREGATE-0000000003
    Processor: KSTREAM-MAPVALUES-0000000008 (stores: [])
      --> KSTREAM-PEEK-0000000009
      <-- KTABLE-TOSTREAM-0000000007
    Processor: KSTREAM-PEEK-0000000009 (stores: [])
      --> KSTREAM-SINK-0000000010
      <-- KSTREAM-MAPVALUES-0000000008
    Sink: KSTREAM-SINK-0000000010 (topic: kafka-workshop-top-10-stream-count)
      <-- KSTREAM-PEEK-0000000009
```

### Solve Stateless Exercises

Choose one or both of the [stateless exercises](https://github.com/schroedermatt/stream-processing-workshop/tree/main/src/main/java/org/improving/workshop/exercises/stateless) to solve. The goals are listed at the top of the class.

It can be helpful to first map out your topology. Download [this file](https://github.com/schroedermatt/stream-processing-workshop/blob/main/assets/excalidraw/workshop-template.excalidraw) and open it up in https://excalidraw.com and sketch out the expected topology.

A test case for each exercise is [already written for you](https://github.com/schroedermatt/stream-processing-workshop/tree/main/src/test/groovy/org/improving/workshop/exercises/stateless). Work on the stream until the test is green (passing).

### Solve Stateful Exercises

If you've solved the stateless exercises, explore the stateful exercises. These will require a little more thought and up front design.

It can be helpful to first map out your topology. Download [this file](https://github.com/schroedermatt/stream-processing-workshop/blob/main/assets/excalidraw/workshop-template.excalidraw) and open it up in https://excalidraw.com and sketch out the expected topology.

These also have [tests written for you](https://github.com/schroedermatt/stream-processing-workshop/tree/main/src/test/groovy/org/improving/workshop/exercises/stateful) to get you going. Develop the stream until its test is green (passing).