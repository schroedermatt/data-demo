package org.msse.demo.mockdata.faker;

import net.datafaker.Faker;
import org.msse.demo.mockdata.domain.Phone;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("mockdata")
public class PhoneFaker extends BaseFaker {

  public PhoneFaker(Faker faker) {
    super(faker);
  }

  public Phone generate(String customerId) {
    return new Phone(
            randomID(),
            customerId,
            faker.options().nextElement(List.of("RESID", "UNLST", "BUS", "FAX", "CELL", "U")),
            faker.options().nextElement(List.of("U", "Y", "N")),
            faker.options().nextElement(List.of("EST", "CST", "MTN", "PAC", "U", "ATL", "AST", "HST")),
            faker.phoneNumber().extension(),
            faker.phoneNumber().phoneNumber()
    );
  }
}
