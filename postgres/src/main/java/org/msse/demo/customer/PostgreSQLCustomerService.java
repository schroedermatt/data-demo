package org.msse.demo.customer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.msse.demo.customer.address.AddressMapper;
import org.msse.demo.customer.address.AddressRepository;
import org.msse.demo.customer.email.EmailMapper;
import org.msse.demo.customer.email.EmailRepository;
import org.msse.demo.customer.phone.PhoneMapper;
import org.msse.demo.customer.phone.PhoneRepository;
import org.msse.demo.customer.profile.CustomerMapper;
import org.msse.demo.customer.profile.CustomerRepository;
import org.msse.demo.mockdata.customer.CustomerService;
import org.msse.demo.mockdata.customer.FullCustomer;
import org.msse.demo.mockdata.customer.profile.CustomerFaker;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("postgres")
@RequiredArgsConstructor
public class PostgreSQLCustomerService implements CustomerService {
    private final CustomerFaker customerFaker;

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;
    private final EmailMapper emailMapper;
    private final EmailRepository emailRepository;
    private final PhoneMapper phoneMapper;
    private final PhoneRepository phoneRepository;

    @Override
    public FullCustomer createCustomer() {
        return createCustomer(null);
    }

    @Override
    public FullCustomer createCustomer(String customerId) {
        FullCustomer customer = customerFaker.generateFull(customerId);

        log.info("Saving Customer to PostgreSQL - ID={}", customer.customer().id());

        customerRepository.save(customerMapper.mapToEntity(customer.customer()));
        addressRepository.save(addressMapper.mapToEntity(customer.address(), customer.customer()));
        emailRepository.save(emailMapper.mapToEntity(customer.email(), customer.customer()));
        phoneRepository.save(phoneMapper.mapToEntity(customer.phone(), customer.customer()));

        return customer;
    }
}
