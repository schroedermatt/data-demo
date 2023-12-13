package org.msse.demo.mockdata.customer.address;

import net.datafaker.Faker;
import org.msse.demo.mockdata.faker.BaseFaker;
import org.msse.demo.mockdata.load.CityData;
import org.msse.demo.mockdata.util.Loader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class AddressFaker extends BaseFaker {

  public static final String RESID_TYPE_CODE = "RESID";
  public static final String VENUE_TYPE_CODE = "VENUE";

  private Random random = new Random();
  private List<CityData> cities;

  public AddressFaker(Faker faker) {
    super(faker);
    cities = Loader.loadCityData();
  }

  public Address generateCustomerAddress(String customerId) {
    return generateCustomerAddress(randomId(), customerId);
  }

  public Address generateCustomerAddress(String addressId, String customerId) {
    return generateAddress(addressId, customerId, RESID_TYPE_CODE);
  }

  public Address generateVenueAddress(String customerId) {
    return generateAddress(randomId(), customerId, VENUE_TYPE_CODE);
  }

  private int index() {
    return random.nextInt(0, cities.size());
  }

  private Address generateAddress(String addressId, String customerId, String addressType) {

    CityData cityData = cities.get(index());

    return new Address(
            addressId,
            customerId,
            "US",
            addressType,
            faker.address().streetAddress(),
            // todo - randomly add mailbox
            faker.address().mailBox(),
            cityData.city(),
            cityData.state_abbr(),
            cityData.zip(),
            faker.number().digits(4),
            "US",
            null,
            null
    );
  }


}
