package org.msse.demo.customer;

import lombok.extern.slf4j.Slf4j;
import org.msse.demo.address.AddressMapperImpl;
import org.msse.demo.address.AddressRepository;
import org.msse.demo.email.EmailMapperImpl;
import org.msse.demo.email.EmailRepository;
import org.msse.demo.mockdata.customer.CustomerFaker;
import org.msse.demo.mockdata.customer.CustomerService;
import org.msse.demo.mockdata.customer.FullCustomer;
import org.msse.demo.phone.PhoneMapperImpl;
import org.msse.demo.phone.PhoneRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("postgres")
public class PostgreSQLCustomerService implements CustomerService {
    private final CustomerFaker customerFaker;

    private final CustomerMapperImpl customerMapper;
    private final CustomerRepository customerRepository;
    private final AddressMapperImpl addressMapper;
    private final AddressRepository addressRepository;
    private final EmailMapperImpl emailMapper;
    private final EmailRepository emailRepository;
    private final PhoneMapperImpl phoneMapper;
    private final PhoneRepository phoneRepository;

    public PostgreSQLCustomerService(CustomerFaker customerFaker, CustomerMapperImpl customerMapper, CustomerRepository customerRepository, AddressMapperImpl addressMapper, AddressRepository addressRepository, EmailMapperImpl emailMapper, EmailRepository emailRepository, PhoneMapperImpl phoneMapper, PhoneRepository phoneRepository) {
        this.customerFaker = customerFaker;

        this.customerMapper = customerMapper;
        this.customerRepository = customerRepository;

        this.addressMapper = addressMapper;
        this.addressRepository = addressRepository;

        this.emailMapper = emailMapper;
        this.emailRepository = emailRepository;

        this.phoneMapper = phoneMapper;
        this.phoneRepository = phoneRepository;
    }

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
