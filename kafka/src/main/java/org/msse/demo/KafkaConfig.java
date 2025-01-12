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

        props.put(ProducerConfig.CLIENT_ID_CONFIG, cluster.clientId);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, cluster.bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        if (cluster.cloud) {
            props.put("security.protocol", cluster.securityProtocol);
            props.put("sasl.jaas.config", cluster.saslJaasConfig);
            props.put("sasl.mechanism", cluster.saslMechanism);
        }

        return props;
    }

    @Bean
    public Producer<String, Object> genericJsonProducer() {
        return new KafkaProducer<>(producerConfigs());
    }


    @ConfigurationProperties(prefix = "kafka.cluster")
    public record Cluster(
            String bootstrapServers,
            boolean cloud,
            String securityProtocol,
            String saslJaasConfig,
            String saslMechanism,
            String clientId
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
            String streams,
            String advertisements
    ) {}
}
