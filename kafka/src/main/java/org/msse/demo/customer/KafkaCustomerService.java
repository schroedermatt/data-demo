package org.msse.demo.customer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.msse.demo.KafkaConfig;
import org.msse.demo.mockdata.customer.CustomerService;
import org.msse.demo.mockdata.customer.FullCustomer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("kafka")
@RequiredArgsConstructor
public class KafkaCustomerService implements CustomerService {

    private final CustomerCache customerCache;
    private final KafkaConfig.Topics topics;
    private final Producer<String, Object> kafkaProducer;

    @Override
    public FullCustomer createCustomer() {
        FullCustomer customer = customerCache.generateCustomer();

        produceCustomer(customer);

        return customer;
    }

    @Override
    public FullCustomer createCustomer(String customerId) {
        FullCustomer customer = customerCache.getCustomer(customerId);

        produceCustomer(customer);

        return customer;
    }

    @Override
    public long customerCount() {
        return customerCache.customerCount();
    }

    @SneakyThrows
    private void produceCustomer(FullCustomer customer) {
        log.info("Producing Customer ({}) to Kafka", customer.customer().id());
        kafkaProducer.send(new ProducerRecord<>(topics.customers(), customer.customer().id(), customer.customer())).get();

        log.info("Producing Address ({}) for Customer ({}) to Kafka", customer.address().id(), customer.customer().id());
        kafkaProducer.send(new ProducerRecord<>(topics.addresses(), customer.address().id(), customer.address())).get();

        log.info("Producing Phone ({}) for Customer ({}) to Kafka", customer.phone().id(), customer.customer().id());
        kafkaProducer.send(new ProducerRecord<>(topics.phones(), customer.phone().id(), customer.phone())).get();

        log.info("Producing Email ({}) for Customer ({}) to Kafka", customer.email().id(), customer.customer().id());
        kafkaProducer.send(new ProducerRecord<>(topics.emails(), customer.email().id(), customer.email())).get();
    }
}
