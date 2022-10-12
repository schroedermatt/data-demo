package org.msse.demo.mockdata.faker;

import net.datafaker.Faker;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;

public class BaseFaker {

  protected final Faker faker;
  public BaseFaker(Faker faker) {
    this.faker = faker;
  }

  //
  // SHARED UTILITIES
  //

  DateTimeFormatter MONTH_DAY_ONLY = DateTimeFormatter.ofPattern("MMdd");

  public ZonedDateTime randomDateTimeAfter(LocalDateTime localDateTime, TemporalAmount maxAfter) {
    long millisAfter = faker.number().numberBetween(0, Duration.from(maxAfter).toMillis());
    return ZonedDateTime.of(localDateTime.plus(Duration.ofMillis(millisAfter)), ZoneId.of("UTC"));
  }

  public LocalDate randomDate(LocalDate from, LocalDate to) {
    long randomDays = faker.number().numberBetween(0, ChronoUnit.DAYS.between(from, to));
    return from.plusDays(randomDays);
  }

  public LocalDate randomDateBefore(LocalDate date, TemporalAmount maxBefore) {
    return randomDate(date.minus(maxBefore), date);
  }

  public LocalDate randomDateAfter(LocalDate date, TemporalAmount maxAfter) {
    return randomDate(date, date.plus(maxAfter));
  }

  public LocalTime randomTime() {
    return randomTimeBetween(LocalTime.MIN, LocalTime.MAX);
  }

  public LocalTime randomTimeBefore(LocalTime time) {
    return randomTimeBetween(LocalTime.MIN, time);
  }

  public LocalTime randomTimeAfter(LocalTime time) {
    return randomTimeBetween(time, LocalTime.MAX);
  }

  public LocalTime randomTimeBetween(LocalTime from, LocalTime to) {
    if (from.isAfter(to)) {
      throw new IllegalArgumentException("from must be before to");
    }
    long randomSecondBetween =
        faker.number().numberBetween(from.toSecondOfDay(), to.toSecondOfDay());
    return LocalTime.ofSecondOfDay(randomSecondBetween);
  }

  public String randomMonthDayString() {
    return randomDateBefore(LocalDate.now(), Period.ofYears(1)).format(MONTH_DAY_ONLY);
  }

  public String randomOperatorIdCode() {
    // not sure what all the possibilities here are... this is a shot in the dark based on a small
    // amount of sample data
    return faker.regexify("[$ABCD][A-Z][A-Z][A-Z0-9]");
  }

  public String randomID() {
    return faker.number().digits(9);
  }
}
