package org.msse.demo.mockdata.music.advertisement;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.msse.demo.mockdata.load.CityPopulation;
import org.msse.demo.mockdata.util.Loader;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AdSpotFaker extends BaseFaker {

  private final static Random RANDOM = new SecureRandom();

  private final static List<Integer> DURATIONS = List.of(15, 30, 45, 60);

  private final List<CityPopulation> cities;

  public AdSpotFaker(Faker faker) {
    super(faker);
    cities = Loader.loadCityPopulation();
  }

  public AdSpot generate(String artistId, String ip) {

    final CityPopulation city = randomCity();


    return new AdSpot(
            UUID.randomUUID().toString(),
            artistId,
            city.city(),
            city.stateAbbr(),
            DURATIONS.get(RANDOM.nextInt(DURATIONS.size())),
            Instant.now()
    );
  }

  private CityPopulation randomCity () {
    return cities.get(RANDOM.nextInt(cities.size()));
  }
}
