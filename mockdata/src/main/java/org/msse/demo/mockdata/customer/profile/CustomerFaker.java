package org.msse.demo.mockdata.customer.profile;

import lombok.Getter;
import net.datafaker.Faker;
import org.msse.demo.mockdata.customer.FullCustomer;
import org.msse.demo.mockdata.customer.address.AddressFaker;
import org.msse.demo.mockdata.customer.email.EmailFaker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.msse.demo.mockdata.customer.phone.PhoneFaker;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@Service
@Getter
public class CustomerFaker extends BaseFaker {
  private static final LocalDate DATE_1900 = LocalDate.of(1900, 1, 1);
  private static final LocalDate DATE_1980 = LocalDate.of(1980, 1, 1);

  private final AddressFaker addressFaker;
  private final EmailFaker emailFaker;
  private final PhoneFaker phoneFaker;

  public CustomerFaker(Faker faker, AddressFaker addressFaker, EmailFaker emailFaker, PhoneFaker phoneFaker) {
    super(faker);
    this.addressFaker = addressFaker;
    this.emailFaker = emailFaker;
    this.phoneFaker = phoneFaker;
  }

  public Customer generate() {
    return generate(randomId());
  }

  public Customer generate(String customerId) {
    LocalDate birthDate = randomDate(DATE_1900, DATE_1980);
    LocalDate customerBeginDate = randomDate(birthDate.plusYears(faker.number().numberBetween(14, 40)), now());

    String firstName = faker.name().firstName();
    String middleName = faker.name().firstName();
    String lastName = faker.name().lastName();

    return new Customer(
            customerId,
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

  public FullCustomer generateFull() {
    return generateFull(null);
  }

  public FullCustomer generateFull(String customerId) {
    Customer customer = generate(customerId);
    return new FullCustomer(
            customer,
            addressFaker.generateCustomerAddress(customer.id()),
            emailFaker.generate(customer.id()),
            phoneFaker.generate(customer.id())
    );
  }
}
