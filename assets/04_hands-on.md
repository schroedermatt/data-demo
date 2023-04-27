# Hands On: Schema Registry

## Goals

1. Start Environment (Kafka - Confluent Platform)
2. Register Schema(s)
3. Produce & Consume Data

> NOTE: `git pull main` on data-demo before continuing!

### Start Kafka Cluster

The data-demo project has a [kafka environment](https://github.com/schroedermatt/data-demo/blob/main/kafka/local/cluster/docker-compose-confluent.yml) that can be started via Docker.

```bash
# run from the root of the data-demo repository
./gradlew kafkaComposeUp
```

Validate that Kafka (Confluent) is up and running by navigating to [Control Center](http://localhost:9021).

Oh, and we now have [Schema Registry](https://docs.confluent.io/platform/current/schema-registry/index.html)! Let's validate that's up as well with a couple commands.

```bash
# see all registered subjects (schemas) -- there shouldn't be any yet
curl -X GET http://localhost:8081/subjects

[]

--

# get the schema registry config (compatibility type) -- defaults to BACKWARD
curl -X GET http://localhost:8081/config

{
  "compatibilityLevel": "BACKWARD"
}
```

### Register Schema

Here's a simple Avro schema with 3 required fields, fname, lname, and age.

```json
{
    "type": "record",
    "namespace": "org.msse.mockdata",
    "name": "User",
    "fields": [
        { "name": "fname", "type": "string" },
        { "name": "lname", "type": "string" },
        { "name": "age", "type": "int" }
    ]
}
```

Register the schema under the subject `"user-v1"`. You can think of Schema Registry as a key/value store where the "subject" is the key.

```bash
# run from the root of the data-demo project
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    -d @assets/avro/user.v1.0.avro \
    http://localhost:8081/subjects/user-v1/versions

{ "id":1 }
```

The Schema Registry API allows you to easily explore what's registered. Here are 4 common endpoints you can call to explore what's in the registry.

Try each of these -

```bash
# get all available subjects (schemas)
> curl -X GET http://localhost:8081/subjects

[ "user-v1" ]

--
# get the registered versions for a specific subject
> curl -X GET http://localhost:8081/subjects/user-v1/versions

[ 1 ]

-- 
# get a specific registered version of a subject
> curl -X GET http://localhost:8081/subjects/user-v1/versions/1

{
  "id": 1,
  "version": 1,
  "subject": "user-v1",
  "schema": "{...}"
}

-- 
# get a specific registered version's schema for a subject
> curl -X GET http://localhost:8081/subjects/user-v1/versions/1/schema

{
  "type": "record",
  "name": "User",
  "namespace": "org.msse.mockdata",
  "fields": [ .. ]
}
```

### Produce & Consume Avro

Now that the schema is registered, we can produce & consume events that abide by the Avro schema. 

The `kafka-avro-console-producer` & `kafka-avro-console-consumer` CLIs are baked into the schema-registry Docker container. We are going to `exec` onto the container to run the CLI commands.

First, produce an Avro event with the command below. We are going to specifically target the schema that was previously registered by id (`value.schema.id=1`). You can also supply the schema when producing the event and it can be registered during the event production lifecycle.

```shell
docker exec --interactive --tty schema-registry \
kafka-avro-console-producer \
    --broker-list broker:9092 \
    --topic test.topic.avro  \
    --property schema.registry.url=http://schema-registry:8081 \
    --property value.schema.id=1

# copy the below sample, paste and hit enter
{"fname":"john", "lname": "doe", "age": 50}

# now break it by pasting json that's missing 'age'
{"fname":"john", "lname": "doe"}
```

If you provide JSON that doesn't align with the schema, the `kafka-avro-console-producer` will crash with a 1/2 useful error message (I wish it did a little better job of explaining why it crashed but 🤷‍)

Now, consume the Avro event!

```shell
docker exec --interactive --tty schema-registry \
kafka-avro-console-consumer \
    --topic test.topic.avro \
    --bootstrap-server broker:9092 \
    --property schema.registry.url=http://schema-registry:8081 \
    --from-beginning
```

You can also open up [Control Center](http://localhost:9021/) and find the message on the topic.

- Click into the "controlcenter.cluster"
- Select "Topics" in the left menu
- Select "test.topic.avro"
- Select the "Messages" tab
- Use the "Jump to Offset" feature, enter 0 -- you'll have to try each partition to find the partition with your event

### Register a Compatible Schema Change

Whoops! Our initial schema wasn't complete. We don't need `age` and we forgot middle name (`mname`). Let's make these edits and register a new version.

**COMPATIBILITY TYPE RECAP**

> BACKWARD (the default) compatibility allows the deletion of fields (required or optional) and addition of OPTIONAL fields.

> FORWARD Compatibility allows the deletion of OPTIONAL fields and addition of fields (required or optional).

```json
{
  "type": "record",
  "namespace": "org.msse.mockdata",
  "name": "User",
  "fields": [
      { "name": "fname", "type": "string" },
      { "name": "lname", "type": "string" },
      { "name": "mname", "type": ["null", "string"], "default": "null" }
  ]
}
```

Register the `user.v1.1.avro` schema under the same subject `"user-v1"`. This will result in a new version being created.

```bash
# run from the root of the data-demo project
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    -d @assets/avro/user.v1.1.avro \
    http://localhost:8081/subjects/user-v1/versions

{ "id": 2 }
```

Now we can produce/consume data aligned to either version of the schema.

```bash
curl -X GET http://localhost:8081/subjects/user-v1/versions

[ 1, 2 ]

# check out the newly registered schema version
curl -X GET http://localhost:8081/subjects/user-v1/versions/2/schema
```

Produce an event tied to the latest schema (id=2).

```shell
docker exec --interactive --tty schema-registry \
kafka-avro-console-producer \
    --broker-list broker:9092 \
    --topic test.topic.avro  \
    --property schema.registry.url=http://schema-registry:8081 \
    --property value.schema.id=2

# copy the below sample, paste and hit enter
{"fname":"john","lname":"doe","mname":{"string":"lee"}}
```

Wondering why `mname` in our sample was structured as `"mname":{"string":"lee"}`?

Avro's JSON encoding requires that non-null union values be tagged with their intended type. 
This is because unions like ["bytes","string"] and ["int","long"] are ambiguous in JSON, the first
are both encoded as JSON strings, while the second are both encoded as JSON numbers.

### Register an Incompatible Schema Change

We forgot something else, everyone **has to** have a nickname! Let's add a new **required** field, `nickname`.

Our schema now looks like this -

```json
{
  "type": "record",
  "namespace": "org.msse.mockdata",
  "name": "User",
  "fields": [
      { "name": "fname", "type": "string" },
      { "name": "lname", "type": "string" },
      { "name": "mname", "type": ["null", "string"], "default": "null" },
      { "name": "nickname", "type": "string" }
  ]
}
```

However, if you try to register it, you'll get back a `409` with a message explaining that the new field needs to have a default due to the `BACKWARD` compatibility type being in use.

```bash
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    -d @assets/avro/user.v2.0.avro \
    http://localhost:8081/subjects/user-v1/versions

{
  "error_code": 409,
  "message": "Schema being registered is incompatible with an earlier schema for subject \"user-v1\", 
    details: [
      Incompatibility{type:READER_FIELD_MISSING_DEFAULT_VALUE, 
      location:/fields/4, 
      message:nickname, 
      reader:{..}
    ]"
}
```

If you were planning to automate the schema registry process, you might first add a compatibility check. This can be done directly through the compatibility API.

The below command is checking the compatibility of our schema with the required `nickname` field against the latest registered version (2).

```bash
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    -d @assets/avro/user.v2.0.avro \
    http://localhost:8081/compatibility/subjects/user-v1/versions/latest
  
{
  "is_compatible": false
}
```

So how do we get this incompatible schema update registered? You've got a couple options.

1. Register the breaking schema under a new subject, `user-v2`. This breaks the versioning chain of the previous subject and creates an entirely new subject starting at version 1 (see image below for a visual of compatible vs incompatible changes).

```bash
# register a new 'data-demo-customers-avro-v2-value' subject
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    -d @assets/avro/user.v2.0.avro \
    http://localhost:8081/subjects/user-v2/versions
    
{ "id":3 }
```

![schema-compatibility](./images/schema-evolution.png)

2. Update the compatibility type for the subject (or globally) to `FORWARD` (allow the deletion of OPTIONAL fields and addition of fields) and register the schema under the same `"user-v1"` subject.

```bash
# update the compatibility for a single subject
curl -X PUT -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    --data '{"compatibility": "FORWARD"}' \
    http://localhost:8081/config/user-v1
    
{
  "compatibility":"FORWARD"
}

--

# register the schema under the 'user-v1' subject (NOT a new v2 subject) 
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    -d @assets/avro/user.v2.0.avro \
    http://localhost:8081/subjects/user-v1/versions

{ "id":3 }

--

# you can also update compatibility requirements globally
curl -X PUT -H "Content-Type: application/vnd.schemaregistry.v1+json" \
    --data '{"compatibility": "FORWARD"}' \
    http://localhost:8081/config
```

## Cleanup

When you're done, feel free to tear down the environment.

```bash
./gradlew kafkaComposeDown
```