# Hands On: Stream Processing

The stream processing hands on exercise will take place in a new repository, [schroedermatt/stream-processing-workshop](https://github.com/schroedermatt/stream-processing-workshop).

The goals of this exercise are listed below.

## Goals

1. Clone the `stream-processing-workshop` repository
2. Run sample stream test to confirm project setup
3. Solve BOTH **Stateless** stream Processing Exercises
4. Solve 1 OF 2 **Stateful** stream Processing Exercises
5. Run "Top Customer Artists" stream against local Kafka

### NOTE
> When using the term "Stream", we're referring to a Java Kafka Streams application that consumes data from Kafka,
> processes the data, and then produces data back to Kafka. Each of the exercise classes is an independent Kafka Streams
> application to simplify the setup needed. Typically, each of these would be their own independent repository and deployable artifact.

## Clone Stream Processing Workshop

First, we're going to clone the `stream-processing-workshop` and get it running. The `data-demo` project has a lot going on, so we've broken out this exercise into its own repository to simplify things.

```bash
git clone https://github.com/schroedermatt/stream-processing-workshop.git
```

## Confirm Project Setup: Run Sample Test

Validate your project setup by running the [`TopCustomerArtistsTest`](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/test/java/org/improving/workshop/samples/TopCustomerArtistsTest.java). This tests a functional Kafka Stream that aggregates and calculates each customer's top artists - [TopCustomerArtists.java](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/samples/TopCustomerArtists.java). 

You can run this test via the `test` Gradle task in your Terminal or directly in your IDE. If using IntelliJ, there will be a "play" button on the left-hand side of your test class.

```bash
./gradlew cleanTest test --tests TopCustomerArtistsTest
```

### 📍 Side Quest: Topology Visualizer

You may be able to mentally visualize exactly what `TopCustomerArtists` is doing by looking at the code but as these applications get more complicated this can be difficult to do.

Luckily, there's a community tool that can assist us in visualizing our topology. This can be helpful in debugging or fact checking your work and making sure the topology looks how you expect it to look.

The topology is [defined/configured on the `StreamsBuilder` instance](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/samples/TopCustomerArtists.java#L43-L75) and before we [start up the application](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/samples/TopCustomerArtists.java#L39), the internal Kafka Streams topology is [logged out](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/Streams.java#L104). 

The topology for this stream is below. However, you may notice that the topology isn't super human-readable so we'll leverage an open source tool to visualize and inspect it.

1. Copy the topology text from below
2. Paste it into [https://zz85.github.io/kafka-streams-viz](https://zz85.github.io/kafka-streams-viz)
3. Compare the image to the [code](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/samples/TopCustomerArtists.java#L43-L75)

```text
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

## Stream Processing Exercises

Now it's your turn to write Kafka Streams apps. For each of these exercises we will leverage Test-Driven Development (TDD), and lucky for you the tests are already in place. Your job is to develop each stream until the tests pass (**do not alter the tests**).

### Solve Stateless Exercises

Solve both of the [stateless exercises](https://github.com/schroedermatt/stream-processing-workshop/tree/main/src/main/java/org/improving/workshop/exercises/stateless).

For each exercise,

1. Uncomment the code within the `configureTopology(final StreamsBuilder builder)` method - [Ex. AddressSortAndStringify](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/exercises/stateless/AddressSortAndStringify.java#L42-L50)
2. Run the tests - [Ex. AddressSortAndStringifyTest](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/test/java/org/improving/workshop/exercises/stateless/AddressSortAndStringifyTest.java)
   - **THESE WILL NOT PASS AND MIGHT NOT EVEN RUN BY DEFAULT** (if lines of code are missing entirely).
3. Develop the Stream to align with the goals/requirements/tests

#### NOTE
> The [Kafka Streams DSL Documentation](https://kafka.apache.org/20/documentation/streams/developer-guide/dsl-api.html#stateless-transformations) contains examples of using each stateless transformation that you will use in these exercises.

#### 1) [AddressSortAndStringify](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/exercises/stateless/AddressSortAndStringify.java) Goals

Create a new address topic that is keyed (record key) by the address state and has a value that is the address condensed into a single String (String Format: `"{line1}, {line2}, {citynm}, {state} {zip5}-{zip4} {countrycd}"`). Lastly, we want to split all 'MN' to one topic and everything else to another topic.

1. Rekey the addresses by state & condense the address object into a single string, removing identifiers (address & customer)
   - Desired String Format: "{line1}, {line2}, {citynm}, {state} {zip5}-{zip4} {countrycd}"
2. Split the stream!
   - IF the state is 'MN', send the record to MN_OUTPUT_TOPIC ("kafka-workshop-priority-addresses")
   - ELSE send the record to DEFAULT_OUTPUT_TOPIC ("kafka-workshop-addresses-by-state")

#### 2) [TargetCustomerFilter](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/exercises/stateless/TargetCustomerFilter.java) Goals

Create a new customer topic that only contains those born in the 1990s (Utopia's most loyal contingent).

1. Filter customers in a target age demographic (born in the 1990s)
   - Hint: The customer date format is "YYYY-MM-DD"
2. Merge streams! There's actually an old Kafka topic lingering (`"data-demo-legacy-customers"`), merge the legacy customers into TOPIC_DATA_DEMO_CUSTOMERS (`"data-demo-customers"`) to ensure you're analyzing ALL customers.

### Solve Stateful Exercises

Following the same approach as above, choose one of the two [stateful exercises](https://github.com/schroedermatt/stream-processing-workshop/tree/main/src/main/java/org/improving/workshop/exercises/stateful) to solve. These will require a little more thought and up front design.

These also have [tests written for you](https://github.com/schroedermatt/stream-processing-workshop/tree/main/src/test/groovy/org/improving/workshop/exercises/stateful) to get you going. Develop the stream until its test is green (passing).

#### NOTE
> The [Kafka Streams DSL Documentation](https://kafka.apache.org/20/documentation/streams/developer-guide/dsl-api.html#stateful-transformations) contains examples of using each stateful transformation that you will use in these exercises.

#### 1) [ArtistTicketCount](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/exercises/stateful/ArtistTicketCount.java) Goals

Count the total tickets sold by each artist.

#### 2) [CustomerStreamCount](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/exercises/stateful/CustomerStreamCount.java) Goals

Count the total streams (i.e. song listens -- yes, this is confusing to use 'stream' here) for each customer

--

It can be helpful to first map out your topology. Download [this file](https://github.com/schroedermatt/stream-processing-workshop/blob/main/assets/excalidraw/workshop-template.excalidraw) and open it up in https://excalidraw.com and sketch out the expected topology.

## Bonus: Running a Stream Locally

If you'd like to actually run one of the Streams against a local Kafka environment, read on.

### Start Kafka & Run `data-demo` Daemon

> See the [Environment Setup Guide](https://github.com/schroedermatt/data-demo/blob/main/assets/00_hands-on-setup.md) from last week for additional details on cloning, configuring, and running [data-demo](https://github.com/schroedermatt/data-demo).

If you haven't already, start up Kafka and begin producing data with the data-demo daemon.

```bash
## RUN FROM data-demo root

# start kafka cluster
./gradlew kafka1ComposeUp

# start data-demo daemon
./gradlew bootRunDaemon
```

### Run "Top Customer Artists" Stream

Once data is being loaded into your local Kafka cluster, start up the [`TopCustomerArtists`](https://github.com/schroedermatt/stream-processing-workshop/blob/main/src/main/java/org/improving/workshop/samples/TopCustomerArtists.java) Kafka Streams application. The stream will produce data to a topic named `"kafka-workshop-top-10-stream-count"`. You can leverage the `kafka-console-consumer` to inspect these events as they are published.

You can run the stream directly in your terminal or IDE (IntelliJ shown below).

```bash
./gradlew -Pstream=org.improving.workshop.samples.TopCustomerArtists run 
```
