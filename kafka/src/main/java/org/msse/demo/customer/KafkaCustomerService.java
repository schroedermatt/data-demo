package org.msse.demo.customer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.msse.demo.KafkaConfig;
import org.msse.demo.mockdata.customer.CustomerService;
import org.msse.demo.mockdata.customer.FullCustomer;
import org.msse.demo.mockdata.customer.address.Address;
import org.msse.demo.mockdata.customer.email.Email;
import org.msse.demo.mockdata.customer.phone.Phone;
import org.msse.demo.mockdata.customer.profile.Customer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("kafka")
@RequiredArgsConstructor
public class KafkaCustomerService implements CustomerService {

    private final CustomerCache customerCache;
    private final KafkaConfig.Topics topics;
    private final Producer<String, Customer> customerProducer;
    private final Producer<String, Address> addressProducer;
    private final Producer<String, Phone> phoneProducer;
    private final Producer<String, Email> emailProducer;

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
        String key = customer.customer().id();

        log.info("Producing Customer ({}) to Kafka", customer.customer().id());
        customerProducer.send(new ProducerRecord<>(topics.customers(), key, customer.customer())).get();

        log.debug("Producing Address ({}) to Kafka", customer.address().id());
        addressProducer.send(new ProducerRecord<>(topics.addresses(), key, customer.address())).get();

        log.debug("Producing Phone ({}) to Kafka", customer.phone().id());
        phoneProducer.send(new ProducerRecord<>(topics.phones(), key, customer.phone())).get();

        log.debug("Producing Email ({}) to Kafka", customer.email().id());
        emailProducer.send(new ProducerRecord<>(topics.emails(), key, customer.email())).get();
    }
}
