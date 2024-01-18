# Hands On: Hello World (Kafka Edition)

In session, we're going to get familiar with Apache Kafka by getting our hands on it in a local environment where you have full control to do whatever you want.

We will begin by starting a Kafka Cluster. Then we'll explore some commonly used CLI commands. We'll finish with a quick look at the Java Kafka Client Libraries.

Have fun and be curious 💡 Ask questions as you go.

### ✅ Goals

- [ ] Start Local Kafka Cluster
- [ ] Explore Kafka CLIs
- [ ] Explore Java Kafka Client Libraries

## 1) Start Local Kafka Cluster

> If you haven't already, `git clone https://github.com/schroedermatt/data-demo`

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


## 2) Produce & Consume Records - CLI

Ok - your environment is up (see previous section if it's not). Now we're going to leverage a few of the CLIs (Command Line Interfaces) to interact with the cluster. The Kafka CLIs offer a variety of capabilities and are a quick way to explore key concepts without any layers of abstraction.

You might be thinking "Oh Em Gee, I didn't download the CLIs!" -- well you're in luck, they come pre-baked on the Kafka Broker Docker Container that **you're already running** so we're going to [`docker exec`](https://docs.docker.com/engine/reference/commandline/exec/) onto the broker and run the commands from inside that container 🥷 the `/kafka-bin` directory has helper scripts that will handle exec'ing onto the broker to run the commands.

### Create Topic

First, create a Kafka Topic by running the command below in your terminal.

> Command: [`kafka-topics --create`](https://docs.confluent.io/kafka/operations-tools/topic-operations.html#add-a-topic)

```bash
# run from root dir of data-demo repo
./kafka-bin/kafka-topics --create --topic test-topic

--

Created topic test-topic.
```

### Produce Records (No Key)

The topic exists, now produce a record.

Enter the `kafka-console-producer` command below and then type text + press enter to submit. Each line will be its own Kafka record. **Hit Ctrl + C to exit the producer.**

```bash
# run from root dir of data-demo repo
./kafka-bin/kafka-console-producer --topic test-topic

--

>hello
>world
>bye

# CONTROL+C to exit
```

### Produce Records (With Key)

The previous command produced records **without keys**, but we now know keys are important when ordering matters!

To produce events with keys, we'll use a similar command as before but add a couple more properties. Enter the command below and then type text + hit enter. The text before the comma will be the record key and the text after the comma will be the record value.

```bash
# run from root dir of data-demo repo
./kafka-bin/kafka-console-producer --topic test-topic \
  --property parse.key=true \
  --property key.separator=","

--

>hello,world1
>bye,world2

# CONTROL+C to exit
```

### Consume Records (Without Key)

There are records on `test-topic`, now we will consume them 🍕

We'll leverage the `kafka-console-consumer` CLI to do this. Enter the below command to consume the records. The `--from-beginning` will ensure that our consumer starts at `earliest` on the topic. By default, consumers start at `latest`. Take out the `--from-beginning` and your consumer will only consume records that arrive after it starts listening.

```bash
# run from root dir of data-demo repo
./kafka-bin/kafka-console-consumer --topic test-topic \
                       --group test-group \
                       --from-beginning

--

hello
world
bye
world1
world2

# CONTROL+C to exit
^CProcessed a total of 5 messages
```

### Consume Records (With Key)

**Hold up, the record keys are missing 😤**

To include keys, we will modify the command and add a couple more properties. Run the below command to reconsume the events and include keys (if exists). Non-existent keys will be printed out as "null".

```bash
# run from root dir of data-demo repo
./kafka-bin/kafka-console-consumer \
  --topic test-topic \
  --group test-group-with-keys \
  --from-beginning \
  --property print.key=true \
  --property key.separator="-"
  
--

null-hello
null-world
null-bye
hello-world
bye-world

# CONTROL+C to exit
^CProcessed a total of 5 messages
```

We're all hackerz now, using CLIs to kill the mainframe and bring down the internet (or produce and consume a few records). If you get tired of hacking, there's are tools to assist you. When we run our full kafka stack (`./gradlew kafkaComposeUp`) we start up [kpow](https://factorhouse.io/kpow/), an easy to use Apache Kafka Web UI.

### Consumer Group Exploration

We talked about consumer groups enabling seamless distributed processing. Let's inspect your test consumer group in a little more in depth.

We're going to list out all groups and then describe our specific group. This will tell us details about the topics/partitions it's consuming, the beginning/ending offset of each partition, and where the group is at on it. These details factor into the 'lag' (the number of records between current offset and end offset) a consumer current has. Lag is an important metric when monitoring Kafka applications.

Run the commands in the code snippet below in order. Each command has a comment above it explaining what we're doing.

```bash
###########################
# list all consumer groups (we should see 'test-group' in the list)
./kafka-bin/kafka-consumer-groups --list

--
# output
test-group
test-group-with-keys

###########################
# describe the 'test-group' consumer group
./kafka-bin/kafka-consumer-groups --group test-group --describe

--
# output with GROUP, CONSUMER-ID, HOST, CLIENT-ID removed for brevity
TOPIC       PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG
test-topic  0          2               2               0
test-topic  1          3               3               0
test-topic  2          0               0               0
test-topic  3          0               0               0


###########################
# reset a consumer group back to "earliest" (beginning) on 'test-topic'
./kafka-bin/kafka-consumer-groups \
  --group test-group \
  --reset-offsets \
  --to-earliest \
  --topic test-topic \
  --execute

--
# SUCCESS output with GROUP removed for brevity
TOPIC        PARTITION  NEW-OFFSET
test-topic   0          0
test-topic   1          0
test-topic   2          0
test-topic   3          0

# ERROR output you will receive if console-consumer was not terminated
Error: Assignments can only be reset if the group 'test-group' is inactive, but the current state is Stable.

###########################
# describe the 'test-group' consumer group again (notice the lag)
./kafka-bin/kafka-consumer-groups --group test-group --describe

--
# output with GROUP, CONSUMER-ID, HOST, CLIENT-ID removed for brevity
TOPIC       PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG
test-topic  0          0               2               2
test-topic  1          0               3               3
test-topic  2          0               0               0
test-topic  3          0               0               0

# if you restarted the consumer above and rerun the consumer-group describe command you should see the lag is back to 0
```

## 3) Produce & Consume Records - Java

So far, we've only worked in the CLI. This is a nice way to quickly get to know Kafka but for longer running apps you're going to need to dive into a client library using your choice language.

Today we're going to look at Java, the most heavily supported language in the Kafka ecosystem.

Go read through this 👉 [SampleJavaKafkaProducer](https://github.com/schroedermatt/data-demo/blob/main/kafka/src/main/java/org/msse/demo/sample/SampleJavaKafkaProducer.java). The comments on the code will give you an overview of what's happening.

To run the code, there's a Gradle task leveraging `javaexec` that will run it.

```bash
# run from the root of data-demo
./gradlew runSampleJavaKafkaProducer

--

... app logs

Message sent successfully to topic: test-topic, partition: 0, offset: 5

... more logs
```

If you see the "Message sent successfully" log, a record was produced! The producer will shut down once it's produced the record.

Now go look at this 👉 [SampleJavaKafkaConsumer](https://github.com/schroedermatt/data-demo/blob/main/kafka/src/main/java/org/msse/demo/sample/SampleJavaKafkaConsumer.java). Read through the comments of the code to get an overview of what's happening.

Running this class can be done the same as the SimpleKafkaProducer (in your IDE or terminal).

```bash
# run from the root of data-demo
./gradlew runSampleJavaKafkaConsumer

--

... app logs

RECORD RECEIVED: key = key-bdca7a82-acee-4ed4-a0fb-0a0d51466fbc, value = value-2023-05-05T17:38:32.357256Z, partition = 0, offset = 5

# CONTROL+C to exit
```

The consumer is a bit clingy and will run until *eliminated* (Ctrl + C) so be assertive and take care of it.