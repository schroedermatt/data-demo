package org.msse.demo.mockdata.music.event;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class EventFaker extends BaseFaker {
  public EventFaker(Faker faker) {
    super(faker);
  }

  public Event generate(String artistId, String venueId, int venueMaxCapacity) {
    return new Event(
            randomId(),
            artistId,
            venueId,
            faker.number().numberBetween(50, venueMaxCapacity),
            faker.date().future(250, TimeUnit.DAYS).toString()
    );
  }
}
