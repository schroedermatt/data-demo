package org.msse.demo.mockdata.music.event;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class EventFaker extends BaseFaker {
  private static final List<String> VENUE_SUFFIX = List.of(" Arena", " Stadium", " Bandshell", " Hall");

  public EventFaker(Faker faker) {
    super(faker);
  }

  public Event generate(String artistId) {
    return new Event(
            randomId(),
            artistId,
            faker.company().name() + faker.options().nextElement(VENUE_SUFFIX),
            String.valueOf(faker.number().numberBetween(2, 75000)),
            faker.date().future(250, TimeUnit.DAYS).toString(),
            String.format("%s:%s:00",
                    faker.number().numberBetween(0, 12),
                    faker.number().numberBetween(0, 60))
    );
  }
}
