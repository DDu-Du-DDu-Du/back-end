package com.ddudu.fixture;

import java.util.Locale;
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

}
