package org.msse.demo.mockdata.music.venue;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueFaker extends BaseFaker {

  public VenueFaker(Faker faker) {
    super(faker);
  }

}
