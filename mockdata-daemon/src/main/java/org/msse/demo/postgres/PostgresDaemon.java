package org.msse.demo.postgres;

import lombok.extern.slf4j.Slf4j;
import org.msse.demo.mockdata.customer.CustomerService;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@Profile({"mockdata","postgres"})
public class PostgresDaemon {

  private final CustomerService customerService;

  public PostgresDaemon(CustomerService customerService) {
    this.customerService = customerService;
  }

  @PostConstruct
  public void init() {
    log.info("POSTGRES DAEMON ENABLED");
  }

  // every 5 seconds
  @Scheduled(cron = "*/5 * * * * *")
  public void createCustomers() {
    customerService.createCustomer();
  }
}
