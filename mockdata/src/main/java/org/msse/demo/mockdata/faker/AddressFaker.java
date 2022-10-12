package org.msse.demo.mockdata.faker;

import net.datafaker.Faker;
import org.msse.demo.mockdata.domain.Address;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("mockdata")
public class AddressFaker extends BaseFaker {

  private static final List<String> ADDRESS_FORMAT_CODES = List.of("US", "MX", "CA", "EU");
  private static final List<String> ADDRESS_TYPE_CODES = List.of("RESID", "BRANCH");

  public AddressFaker(Faker faker) {
    super(faker);
  }

  public Address generate(String customerId) {
    String stateAbbr = faker.address().stateAbbr();

    return new Address(
            randomID(),
            customerId,
            faker.options().nextElement(ADDRESS_FORMAT_CODES),
            faker.options().nextElement(ADDRESS_TYPE_CODES),
            faker.address().mailBox(),
            faker.address().streetAddress(),
            faker.address().cityName(),
            stateAbbr,
            faker.address().zipCodeByState(stateAbbr),
            faker.number().digits(4),
            faker.address().countryCode()
    );
  }
}
