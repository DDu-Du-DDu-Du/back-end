package com.ddudu.fixture;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseFixture {

  protected static final Faker faker = new Faker(Locale.KOREA);

  public static long getRandomId() {
    return getRandomLong(1L, Long.MAX_VALUE);
  }

  public static String getRandomSentenceWithMax(int maxLength) {
    return faker.lorem()
        .maxLengthSentence(maxLength);
  }

  public static String getRandomSentence(int minLength, int maxLength) {
    return faker.lorem()
        .characters(minLength, maxLength);
  }

  public static String getRandomFixedSentence(int length) {
    return faker.lorem()
        .characters(length);
  }

  public static int getRandomInt(int min, int max) {
    return faker.random()
        .nextInt(min, max);
  }

  public static int getRandomPositive() {
    return faker.number()
        .positive();
  }

  public static int getRandomNegative() {
    return faker.number()
        .negative();
  }

  public static long getRandomLong(long min, long max) {
    return faker.random()
        .nextLong(min, max);
  }

  public static String getRandomColor() {
    return faker.color()
        .hex()
        .substring(1);
  }

  public static LocalDateTime getRandomDateTime() {
    return faker.date()
        .birthday()
        .toLocalDateTime();
  }

  public static LocalDateTime getPastDateTime(int pastScope, TimeUnit timeUnit) {
    return faker.date()
        .past(pastScope, timeUnit)
        .toLocalDateTime();
  }

  public static LocalDateTime getFutureDateTime(int futureScope, TimeUnit timeUnit) {
    return faker.date()
        .future(futureScope, timeUnit)
        .toLocalDateTime();
  }

  public static LocalDateTime getRandomDateTimeBetween(LocalDateTime from, LocalDateTime to) {
    Timestamp pastTimestamp = Timestamp.valueOf(from);
    Timestamp futureTimestamp = Timestamp.valueOf(to);

    return faker.date()
        .between(pastTimestamp, futureTimestamp)
        .toLocalDateTime();
  }

  public static LocalDateTime getRandomDateTimeInScopeFromNow(int dayScope) {
    LocalDateTime now = LocalDateTime.now();
    Timestamp past = Timestamp.valueOf(now.minusDays(dayScope));
    Timestamp future = Timestamp.valueOf(now.plusDays(dayScope));

    return faker.date()
        .between(past, future)
        .toLocalDateTime();
  }

}
