package org.msse.demo.daemon;

import lombok.extern.slf4j.Slf4j;
import org.msse.demo.mockdata.address.Address;
import org.msse.demo.mockdata.customer.Customer;
import org.msse.demo.mockdata.email.Email;
import org.msse.demo.mockdata.phone.Phone;
import org.msse.demo.mockdata.address.AddressFaker;
import org.msse.demo.mockdata.customer.CustomerFaker;
import org.msse.demo.mockdata.email.EmailFaker;
import org.msse.demo.mockdata.phone.PhoneFaker;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@Profile({"mockdata","postgres"})
public class PostgresDaemon {

  private final AddressFaker addressFaker;
  private final EmailFaker emailFaker;
  private final PhoneFaker phoneFaker;
  private final CustomerFaker customerFaker;

  public PostgresDaemon(AddressFaker addressFaker, EmailFaker emailFaker, PhoneFaker phoneFaker, CustomerFaker customerFaker) {
    this.addressFaker = addressFaker;
    this.emailFaker = emailFaker;
    this.phoneFaker = phoneFaker;
    this.customerFaker = customerFaker;
  }

  @PostConstruct
  public void init() {
    log.info("POSTGRES DAEMON ENABLED");
  }

  // every 10 seconds
  @Scheduled(cron = "*/10 * * * * *")
  public void generateCustomer() {
    log.info("GENERATING CUSTOMER");

    Customer customer = customerFaker.generate();
    log.info("Customer: {}", customer.toString());

    Address address = addressFaker.generate(customer.id());
    log.info("Address: {}", address.toString());

    Email email = emailFaker.generate(customer.id());
    log.info("Email: {}", email.toString());

    Phone phone = phoneFaker.generate(customer.id());
    log.info("Phone: {}", phone.toString());

    log.info("CUSTOMER GENERATED - ID={}", customer.id());
  }
}
