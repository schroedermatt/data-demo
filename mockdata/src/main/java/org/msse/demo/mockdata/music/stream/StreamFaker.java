package org.msse.demo.mockdata.music.stream;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StreamFaker extends BaseFaker {

  public StreamFaker(Faker faker) {
    super(faker);
  }

  public Stream generate(String customerId, String artistid) {
    return generate(randomId(), customerId, artistid);
  }

  public Stream generate(String streamId, String customerId, String artistid) {
    return new Stream(
            streamId,
            customerId,
            artistid,
            Instant.now().toString()
    );
  }
}
