package org.msse.demo.mockdata.email;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailFaker extends BaseFaker {
  private static final List<String> DOMAINS = List.of("gmail", "outlook", "aol", "yahoo", "icloud", "protonmail", "zoho", "yandex", "gmx", "mail");

  public EmailFaker(Faker faker) {
    super(faker);
  }

  public Email generate(String customerId) {
    return new Email(
            randomgetId(),
            customerId,
            faker.name().username() + "@" + faker.options().nextElement(DOMAINS) + ".com"
    );
  }
}
