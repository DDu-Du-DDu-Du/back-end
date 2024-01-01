package com.ddudu.following.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import com.ddudu.user.domain.User;
import com.ddudu.user.domain.User.UserBuilder;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
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
      assertThatNullPointerException().isThrownBy(construct)
          .withMessage("Follower cannot be null");
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
      assertThatNullPointerException().isThrownBy(construct)
          .withMessage("Followee cannot be null");
    }

    @Test
    void 팔로잉_생성을_성공한다() {
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

      // when
      Following following = Following.builder()
          .follower(expectedFollower)
          .followee(expectedFollowee)
          .build();

      // then
      User actualFollower = following.getFollower();
      User actualFollowee = following.getFollowee();

      assertThat(actualFollower.getId()).isEqualTo(expectedFollower.getId());
      assertThat(actualFollowee.getId()).isEqualTo(expectedFollowee.getId());
    }

  }
}
