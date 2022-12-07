package org.msse.demo.mockdata.music.artist;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArtistFaker extends BaseFaker {
  private static final List<String> GENRES = List.of(
          "Hip Hop & Rap", "Pop", "Rock", "Country", "R&B", "Folk", "Jazz",
          "Heavy Metal", "EDM", "Soul", "Funk", "Reggae", "Disco", "Punk Rock",
          "Classical", "House", "Techno", "Indie Rock", "Grunge", "Ambient", "Gospel", "Latin");

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
            faker.options().nextElement(GENRES)
    );
  }
}
