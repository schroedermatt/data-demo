package org.msse.demo.mockdata.customer;

import net.datafaker.Faker;
import org.msse.demo.mockdata.customer.Customer;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Service
@Profile("mockdata")
public class CustomerFaker extends BaseFaker {
  private static final LocalDate DATE_1900 = LocalDate.of(1900, 1, 1);
  private static final LocalDate DATE_1980 = LocalDate.of(1980, 1, 1);

  public CustomerFaker(Faker faker) {
    super(faker);
  }

  public Customer generate() {
    return generate(null);
  }

  public Customer generate(String customerId) {
    LocalDate birthDate = randomDate(DATE_1900, DATE_1980);
    LocalDate customerBeginDate = randomDate(birthDate.plusYears(faker.number().numberBetween(14, 40)), now());

    String firstName = faker.name().firstName();
    String middleName = faker.name().firstName();
    String lastName = faker.name().lastName();

    return new Customer(
            customerId == null ? randomgetId() : null,
            faker.options().nextElement(List.of("IND", "FAM", "FREE", "PREMIUM")),
            faker.options().nextElement(List.of("U", "N", "F", "M")),
            firstName,
            middleName,
            lastName,
            firstName + " " + middleName + " " + lastName,
            faker.name().suffix(),
            faker.name().title(),
            birthDate.format(ISO_LOCAL_DATE),
            customerBeginDate.format(ISO_LOCAL_DATE)
    );
  }
}
