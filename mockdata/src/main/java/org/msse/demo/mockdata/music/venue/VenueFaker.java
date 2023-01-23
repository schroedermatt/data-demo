package org.msse.demo.mockdata.music.venue;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueFaker extends BaseFaker {
  private static final List<String> VENUE_SUFFIX = List.of(" Arena", " Stadium", " Bandshell", " Hall", " Pavilion", " Field");

  public VenueFaker(Faker faker) {
    super(faker);
  }

  public Venue generate(String addressId) {
    return generate(randomId(), addressId);
  }

  public Venue generate(String venueId, String addressId) {
    return new Venue(
            venueId,
            addressId,
            faker.company().name() + faker.options().nextElement(VENUE_SUFFIX),
            faker.number().numberBetween(5, 5000)
    );
  }
}
