package com.ddudu.user.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.domain.user.domain.Password;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayNameGeneration(ReplaceUnderscores.class)
class PasswordTest {

  static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
  static final Faker faker = new Faker();

  @Test
  void 입력과_실제_비밀번호가_다르면_비교_검사를_실패한다() {
    // given
    String actualPassword = faker.internet()
        .password(8, 40, false, true, true);
    Password password = new Password(actualPassword, PASSWORD_ENCODER);
    String passwordInput = faker.internet()
        .password(8, 40, false, true, true);

    // when
    boolean check = password.check(passwordInput, PASSWORD_ENCODER);

    // then
    assertThat(check).isFalse();
  }

}
