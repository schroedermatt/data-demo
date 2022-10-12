package org.msse.demo.mockdata.faker;

import net.datafaker.Faker;
import org.msse.demo.mockdata.domain.Email;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("mockdata")
public class EmailFaker extends BaseFaker {
  private static final List<String> DOMAINS = List.of("gmail", "outlook", "aol", "yahoo", "icloud", "protonmail", "zoho", "yandex", "gmx", "mail");

  public EmailFaker(Faker faker) {
    super(faker);
  }

  public Email generate(String customerId) {
    return new Email(
            randomID(),
            customerId,
            faker.name().username() + "@" + faker.options().nextElement(DOMAINS) + ".com"
    );
  }
}
