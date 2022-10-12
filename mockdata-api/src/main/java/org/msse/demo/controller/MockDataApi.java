package org.msse.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.msse.demo.customer.CustomerMapperImpl;
import org.msse.demo.customer.CustomerRepository;
import org.msse.demo.customer.CustomerResponse;
import org.msse.demo.mockdata.address.AddressFaker;
import org.msse.demo.mockdata.customer.Customer;
import org.msse.demo.mockdata.customer.CustomerFaker;
import org.msse.demo.mockdata.email.EmailFaker;
import org.msse.demo.mockdata.phone.PhoneFaker;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Profile("mockdata")
public class MockDataApi {

  private final AddressFaker addressFaker;
  private final EmailFaker emailFaker;
  private final PhoneFaker phoneFaker;
  private final CustomerFaker customerFaker;

  private final CustomerMapperImpl customerMapper;
  private final CustomerRepository customerRepository;

  public MockDataApi(AddressFaker addressFaker, EmailFaker emailFaker, PhoneFaker phoneFaker, CustomerFaker customerFaker, CustomerMapperImpl customerMapper, CustomerRepository customerRepository) {
    this.addressFaker = addressFaker;
    this.emailFaker = emailFaker;
    this.phoneFaker = phoneFaker;
    this.customerFaker = customerFaker;

    this.customerMapper = customerMapper;
    this.customerRepository = customerRepository;
  }

  @PostMapping(path = "customers")
  public CustomerResponse generateCustomer() {
    Customer customer = customerFaker.generate();

    log.info("Customer Generated - ID={}", customer.id());

    customerRepository.save(customerMapper.mapToEntity(customer));

    return new CustomerResponse(
            customer,
            addressFaker.generate(customer.id()),
            emailFaker.generate(customer.id()),
            phoneFaker.generate(customer.id())
    );
  }
}
