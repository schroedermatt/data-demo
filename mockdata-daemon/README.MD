# Generate Mock Data

Learning about Event Driven Systems is easier to understand when working with streaming/ever-changing set of data.
Finding real data to stream is a challenge, so instead mock data is generated. To make the data more realistic, the
venues for the demo are real with real addresses and capacity.

The addresses generated for customers are fake, but the city/state/zip information is real. This will become
important when we start to explore more advanced uses of this data when integrating with other systems.

## Generate Data to Kafka

Be sure that one of the kafka instance is running `kafka-1`, `kafka-3`, or `redpanda` and `redis`. This is done with
the gradle tasks of `kafka1ComposeUp`, `kafka3ComposeUp`, `redpandaComposeUp`.

Publishing directly to Kafka is easier, but is less "production like".

`gradle bootRunDaemon -Pspring.profiles.active=kafka`

To see the topics generated, `./bin/kafka-topics --list`.

To see the content on these topics `./bin/kafka-console-consumer --topic <topic> --from-beginning --property print.key=true --propererty key.separator=_`

## Generate Data to Postgres

This represents a more complete enterprise solution. Typically, data is being captured in a transactional database as part
of an existing or legacy system, and data needs to be made accessible to other applications by leveraging Kafka Connect
to bring the data from the transactional database into Kafka.

`gradle bootRunDaemon -Pspring.profiles.active=postgres`

