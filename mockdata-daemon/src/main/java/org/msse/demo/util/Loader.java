package org.msse.demo.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.msse.demo.mockdata.load.CityData;
import org.msse.demo.mockdata.music.venue.Venue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Loader {

  public static List<Venue> loadVenues() {

    final List<Venue> list = new ArrayList<>();

    try {

      CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

      InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("venues.csv");
      InputStreamReader reader = new InputStreamReader(input);

      CSVParser parser = new CSVParser(reader, format);

      //name,street,city,state,zip,lat,lat,capacity

      for (CSVRecord rec : parser) {

        System.out.println(rec);
        final Venue venue = new Venue(
                null,
                rec.get("name"),
                rec.get("street"),
                rec.get("city"),
                rec.get("state"),
                rec.get("zip"),
                Double.parseDouble(rec.get("lat")),
                Double.parseDouble(rec.get("lon")),
                Integer.parseInt(rec.get("capacity"))
        );

        list.add(venue);
      }

      parser.close();

    } catch (final IOException e) {
      throw new RuntimeException(e);
    }

    return list;
  }

  public static List<CityData> loadCityData() {

    final List<CityData> list = new ArrayList<>();

    try {

      CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

      InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("zipcodes.csv");
      InputStreamReader reader = new InputStreamReader(input);

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
