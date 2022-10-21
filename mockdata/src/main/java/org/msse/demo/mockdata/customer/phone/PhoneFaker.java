package org.msse.demo.mockdata.customer.phone;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhoneFaker extends BaseFaker {

  public PhoneFaker(Faker faker) {
    super(faker);
  }

  public Phone generate(String customerId) {
    return generate(randomId(), customerId);
  }

  public Phone generate(String phoneId, String customerId) {
    return new Phone(
            phoneId,
            customerId,
            faker.options().nextElement(List.of("RESID", "UNLST", "BUS", "FAX", "CELL", "U")),
            faker.options().nextElement(List.of("U", "Y", "N")),
            faker.options().nextElement(List.of("EST", "CST", "MTN", "PAC", "U", "ATL", "AST", "HST")),
            faker.phoneNumber().extension(),
            faker.phoneNumber().phoneNumber()
    );
  }
}
