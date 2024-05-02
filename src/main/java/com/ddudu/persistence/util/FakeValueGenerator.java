package com.ddudu.persistence.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FakeValueGenerator {

  private static final Faker FAKER = new Faker();

  public static String email() {
    return FAKER.internet()
        .emailAddress();
  }

  public static String password() {
    return FAKER.internet()
        .password(8, 40, true, true, true);
  }

  public static String username() {
    return FAKER.internet()
        .username();
  }

}
