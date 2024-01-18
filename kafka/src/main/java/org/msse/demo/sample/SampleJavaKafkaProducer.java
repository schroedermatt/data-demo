package org.msse.demo.sample;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.util.Properties;
import java.util.UUID;

public class SampleJavaKafkaProducer {
    public static void main(String[] args) {
        // define the properties for the Kafka consumer
        Properties props = new Properties();

        // where does the producer connect to Kafka (the "bootstrap server(s)")?
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // what types of data will the producer produce for the key and value of the records?
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the Kafka Producer instance
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // create a record to be sent
        ProducerRecord<String, String> record = new ProducerRecord<>(
                "test-topic",
                "key-" + UUID.randomUUID(),
                "value-" + Instant.now());

        // Send the message to Kafka
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                // if something goes wrong, log it out
                if (exception != null) {
                    System.err.println("Failed to send message: " + exception.getMessage());
                    return;
                }

                System.out.println("Message sent successfully to topic: " + metadata.topic() +
                        ", partition: " + metadata.partition() +
                        ", offset: " + metadata.offset());
            }
        });

        // close the Kafka producer
        producer.close();
    }
}
