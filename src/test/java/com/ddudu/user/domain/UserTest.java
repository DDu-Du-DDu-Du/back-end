package com.ddudu.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.user.domain.User.UserBuilder;
import com.ddudu.user.exception.UserErrorCode;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@EnableJpaAuditing
@DisplayNameGeneration(ReplaceUnderscores.class)
class UserTest {

  static final Faker faker = new Faker();
  static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

  UserBuilder builderWithEncoder;
  String validEmail;
  String validPassword;
  String validNickname;

  @Autowired
  TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    builderWithEncoder = User.builder()
        .passwordEncoder(PASSWORD_ENCODER);
    validEmail = faker.internet()
        .emailAddress();
    validPassword = faker.internet()
        .password(8, 40, true, true, true);
    validNickname = faker.oscarMovie()
        .character();
  }

  @Nested
  class 유저_생성_테스트 {

    @BeforeEach
    void setUp() {
      builderWithEncoder = User.builder()
          .passwordEncoder(PASSWORD_ENCODER);
      validEmail = faker.internet()
          .emailAddress();
      validPassword = faker.internet()
          .password(8, 40, true, true, true);
      validNickname = faker.oscarMovie()
          .character();
    }

    @Test
    void User_인스턴스를_생성한다() {
      // given
      User expected = builderWithEncoder
          .email(validEmail)
          .password(validPassword)
          .nickname(validNickname)
          .build();

      // when
      User actual = entityManager.persist(expected);

      // then
      assertThat(actual).usingRecursiveComparison()
          .isEqualTo(expected);
    }

    @Test
    void User_엔티티_Id에_Auto_Increment가_적용된다() {
      // given
      String differentEmail = faker.internet()
          .emailAddress();
      User first = builderWithEncoder
          .email(validEmail)
          .password(validPassword)
          .nickname(validNickname)
          .build();
      User second = builderWithEncoder
          .email(differentEmail)
          .password(validPassword)
          .nickname(validNickname)
          .build();

      // when
      User persistedFirst = entityManager.persist(first);
      User persistedSecond = entityManager.persist(second);

      // then
      assertThat(persistedSecond.getId()).isGreaterThan(persistedFirst.getId());
    }

    @ParameterizedTest(name = "유효하지 않은 이메일 : {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "email", "email@example", "email@example.", "email@example.com."})
    void 유효하지_않은_이메일의_유저_생성을_실패한다(String email) {
      // given
      UserBuilder userBuilder = builderWithEncoder
          .email(email)
          .password(validPassword)
          .nickname(validNickname);

      // when
      ThrowingCallable construct = userBuilder::build;

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct);
    }

    @ParameterizedTest(name = "유효하지 않은 비밀번호 : {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "short", "한글1234!%", "withoutDigits!", "withoutSpecial123"})
    void 유효하지_않은_비밀번호의_유저_생성을_실패한다(String password) {
      // given
      UserBuilder userBuilder = builderWithEncoder
          .email(validEmail)
          .password(password)
          .nickname(validNickname);

      // when
      ThrowingCallable construct = userBuilder::build;

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct);
    }

    @ParameterizedTest(name = "유효하지 않은 닉네임 : {0}")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "nickname legnth over 20"})
    void 유효하지_않은_닉네임의_유저_생성을_실패한다(String nickname) {
      // given
      UserBuilder userBuilder = builderWithEncoder
          .email(validEmail)
          .password(validPassword)
          .nickname(nickname);

      // when
      ThrowingCallable construct = userBuilder::build;

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct);
    }

    @ParameterizedTest(name = "유효하지 않은 아이디 : {0}")
    @EmptySource
    @ValueSource(strings = {" ", "username legnth over 20"})
    void Optional_Username이_주어지고_유효하지_않은_값이면_유저_생성을_실패한다(String optionalUsername) {
      // given
      UserBuilder userBuilder = builderWithEncoder
          .email(validEmail)
          .password(validPassword)
          .nickname(validNickname)
          .optionalUsername(optionalUsername);

      // when
      ThrowingCallable construct = userBuilder::build;

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct);
    }

    @Test
    void 자기소개가_기입되고_유효하지_않은_값이면_유저_생성을_실패한다() {
      // given
      String intro = faker.harryPotter()
          .quote()
          .repeat(3);
      UserBuilder userBuilder = builderWithEncoder
          .email(validEmail)
          .password(validPassword)
          .nickname(validNickname)
          .introduction(intro);

      // when
      ThrowingCallable construct = userBuilder::build;

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct)
          .withMessage(UserErrorCode.EXCESSIVE_INTRODUCTION_LENGTH.getMessage());
    }

    @Test
    void 입력된_비밀번호는_인코딩_된다() {
      // given
      UserBuilder userBuilder = builderWithEncoder
          .password(validPassword)
          .email(validEmail)
          .nickname(validNickname);

      // when
      User user = userBuilder.build();

      // then
      Password password = user.getPassword();
      assertThat(password.check(validPassword, PASSWORD_ENCODER)).isTrue();
    }

  }

}
