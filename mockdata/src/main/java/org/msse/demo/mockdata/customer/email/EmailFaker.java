package org.msse.demo.mockdata.customer.email;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailFaker extends BaseFaker {
  private static final List<String> DOMAINS = List.of("gmail", "outlook", "aol", "yahoo", "icloud", "protonmail", "zoho", "yandex", "gmx", "mail");

  public EmailFaker(Faker faker) {
    super(faker);
  }

  public Email generate(String customerId) {
    return generate(randomId(), customerId);
  }

  public Email generate(String emailId, String customerId) {
    return new org.msse.demo.mockdata.customer.email.Email(
            emailId,
            customerId,
            faker.name().username() + "@" + faker.options().nextElement(DOMAINS) + ".com"
    );
  }
}
