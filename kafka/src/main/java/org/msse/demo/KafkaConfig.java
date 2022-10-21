package org.msse.demo;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.msse.demo.serdes.JsonSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

@Profile("kafka")
@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final Cluster cluster;

    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    @Bean
    public Producer<String, Object> genericJsonProducer() {
        return new KafkaProducer<>(producerConfigs());
    }


    @ConfigurationProperties(prefix = "kafka.cluster")
    public record Cluster(
            String bootstrapServers
    ) {}

    @ConfigurationProperties(prefix = "kafka.topics")
    public record Topics(
            String customers,
            String addresses,
            String phones,
            String emails,
            String artists,
            String venues,
            String events,
            String tickets,
            String streams
    ) {}
}
