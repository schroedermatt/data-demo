package org.msse.demo.controller;

import org.msse.demo.domain.CustomerResponse;
import org.msse.demo.mockdata.domain.Customer;
import org.msse.demo.mockdata.faker.AddressFaker;
import org.msse.demo.mockdata.faker.CustomerFaker;
import org.msse.demo.mockdata.faker.EmailFaker;
import org.msse.demo.mockdata.faker.PhoneFaker;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("mockdata")
public class MockDataApi {

  private final AddressFaker addressFaker;
  private final EmailFaker emailFaker;
  private final PhoneFaker phoneFaker;
  private final CustomerFaker customerFaker;

  public MockDataApi(AddressFaker addressFaker, EmailFaker emailFaker, PhoneFaker phoneFaker, CustomerFaker customerFaker) {
    this.addressFaker = addressFaker;
    this.emailFaker = emailFaker;
    this.phoneFaker = phoneFaker;
    this.customerFaker = customerFaker;
  }

  @PostMapping(path = "customers")
  public CustomerResponse generateCustomer() {
    Customer customer = customerFaker.generate();

    return new CustomerResponse(
            customer,
            addressFaker.generate(customer.id()),
            emailFaker.generate(customer.id()),
            phoneFaker.generate(customer.id())
    );
  }
}
