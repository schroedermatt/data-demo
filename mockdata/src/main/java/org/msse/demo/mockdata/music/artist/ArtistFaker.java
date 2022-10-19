package org.msse.demo.mockdata.music.artist;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.stereotype.Service;

@Service
public class ArtistFaker extends BaseFaker {
  public ArtistFaker(Faker faker) {
    super(faker);
  }

  public Artist generate() {
    return generate(randomId());
  }

  public Artist generate(String id) {
    return new Artist(
            id,
            faker.artist().name(),
            faker.music().genre()
    );
  }
}
