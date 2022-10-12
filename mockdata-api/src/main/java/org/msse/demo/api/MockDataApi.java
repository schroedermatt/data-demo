package org.msse.demo.api;

import lombok.extern.slf4j.Slf4j;
import org.msse.demo.mockdata.customer.CustomerService;
import org.msse.demo.mockdata.customer.FullCustomer;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Profile("mockdata")
public class MockDataApi {

  private final CustomerService customerService;

  public MockDataApi(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostMapping(path = "customers")
  public FullCustomer generateCustomer() {
    return customerService.createCustomer();
  }
}
