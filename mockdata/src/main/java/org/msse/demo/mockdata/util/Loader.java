package org.msse.demo.mockdata.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.msse.demo.mockdata.customer.address.Address;
import org.msse.demo.mockdata.load.CityData;
import org.msse.demo.mockdata.music.venue.Venue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static org.msse.demo.mockdata.customer.address.AddressFaker.VENUE_TYPE_CODE;

@Slf4j
public class Loader {
  public static Map<Venue, Address> loadVenues() {

    // keep track of the venues loaded
    final Map<Venue, Address> map = new HashMap<>();

    try {
      CSVFormat format = CSVFormat.Builder.create().setDelimiter(",").setHeader().build();

      InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("venues.csv");
      InputStreamReader reader = new InputStreamReader(input);

      CSVParser parser = new CSVParser(reader, format);

      //name,street,city,state,zip,lat,lat,capacity

      for (CSVRecord rec : parser) {

        log.info("CSV Record: {}", rec);

        // todo - line up with the other IDs format? random 9 digit num
        String addressId = UUID.randomUUID().toString();
        String venueId = UUID.randomUUID().toString();

        final Venue venue = new Venue(
                venueId,
                addressId,
                rec.get("name"),
                Integer.parseInt(rec.get("capacity"))
        );

        final Address venueAddress = new Address(
                addressId,
                null,
                "US",
                VENUE_TYPE_CODE,
                rec.get("street"),
                null,
                rec.get("city"),
                rec.get("state"),
                rec.get("zip"),
                null,
                "US",
                Double.parseDouble(rec.get("lat")),
                Double.parseDouble(rec.get("lon"))
        );

        map.put(venue, venueAddress);
      }

      parser.close();

    } catch (final IOException e) {
      throw new RuntimeException(e);
    }

    return map;
  }

  public static List<CityData> loadCityData() {

    final List<CityData> list = new ArrayList<>();

    try {

      CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

      InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("zipcodes.csv.gz");
      InputStreamReader reader = new InputStreamReader(new GZIPInputStream(input));

      CSVParser parser = new CSVParser(reader, format);

      for (CSVRecord rec : parser) {
        final CityData cityData = new CityData(
                rec.get("state"),
                rec.get("state_abbr"),
                rec.get("zipcode"),
                rec.get("county"),
                rec.get("city")
        );

        list.add(cityData);
      }

      parser.close();

    } catch (final IOException e) {
      throw new RuntimeException(e);
    }

    return list;
  }
}
