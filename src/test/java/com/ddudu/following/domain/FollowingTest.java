package com.ddudu.following.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.following.exception.FollowingErrorCode;
import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import java.util.Objects;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
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
class FollowingTest {

  static final Faker faker = new Faker();
  static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  UserBuilder builderWithEncoder;
  String validEmail;
  String validPassword;
  String validNickname;

  @Autowired
  TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    builderWithEncoder = User.builder()
        .passwordEncoder(passwordEncoder);
    validEmail = faker.internet()
        .emailAddress();
    validPassword = faker.internet()
        .password(8, 40, true, true, true);
    validNickname = faker.oscarMovie()
        .character();
  }

  @Nested
  class 팔로잉_생성_테스트 {

    @ParameterizedTest
    @NullSource
    void 팔로워가_NULL이면_팔로잉_생성을_실패한다(User follower) {
      // given
      User followee = builderWithEncoder
          .email(validEmail)
          .password(validPassword)
          .nickname(validNickname)
          .build();
      User saved = entityManager.persistFlushFind(followee);

      // when
      ThrowingCallable construct = () -> Following.builder()
          .followee(saved)
          .follower(follower)
          .build();

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct)
          .withMessage(FollowingErrorCode.NULL_FOLLOWER.getMessage());
    }

    @ParameterizedTest
    @NullSource
    void 팔로우_대상이_NULL이면_팔로잉_생성을_실패한다(User followee) {
      // given
      User follower = builderWithEncoder
          .email(validEmail)
          .password(validPassword)
          .nickname(validNickname)
          .build();
      User saved = entityManager.persistFlushFind(follower);

      // when
      ThrowingCallable construct = () -> Following.builder()
          .follower(saved)
          .followee(followee)
          .build();

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct)
          .withMessage(FollowingErrorCode.NULL_FOLLOWEE.getMessage());
    }

    @Test
    void 본인을_팔로우하는_팔로잉_생성을_실패한다() {
      // given
      User follower = builderWithEncoder
          .email(validEmail)
          .password(validPassword)
          .nickname(validNickname)
          .build();

      // when
      ThrowingCallable construct = () -> Following.builder()
          .follower(follower)
          .followee(follower)
          .build();

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct)
          .withMessage(FollowingErrorCode.SELF_FOLLOWING_UNAVAILABLE.getMessage());
    }

    @ParameterizedTest(name = "생성 시 상태: {0}")
    @NullSource
    @ValueSource(strings = "REQUESTED")
    void 팔로잉_생성을_성공한다(String statusName) {
      // given
      User followee = builderWithEncoder
          .email(validEmail)
          .password(validPassword)
          .nickname(validNickname)
          .build();
      String followerEmail = faker.internet()
          .emailAddress();
      User follower = builderWithEncoder
          .email(followerEmail)
          .password(validPassword)
          .nickname(validNickname)
          .build();
      User expectedFollower = entityManager.persistFlushFind(follower);
      User expectedFollowee = entityManager.persistFlushFind(followee);
      FollowingStatus status = Objects.nonNull(statusName) ? FollowingStatus.valueOf(statusName)
          : null;

      // when
      Following following = Following.builder()
          .follower(expectedFollower)
          .followee(expectedFollowee)
          .status(status)
          .build();

      // then
      User actualFollower = following.getFollower();
      User actualFollowee = following.getFollowee();

      assertThat(actualFollower.getId()).isEqualTo(expectedFollower.getId());
      assertThat(actualFollowee.getId()).isEqualTo(expectedFollowee.getId());
    }

  }

}
