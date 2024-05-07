package com.ddudu.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.user.domain.User.UserBuilder;
import com.ddudu.old.user.domain.Following;
import com.ddudu.old.user.domain.FollowingStatus;
import com.ddudu.old.user.exception.FollowingErrorCode;
import com.ddudu.presentation.api.exception.InvalidParameterException;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@DisplayNameGeneration(ReplaceUnderscores.class)
class FollowingTest {

  static final Faker faker = new Faker();
  static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

  UserBuilder builderWithEncoder;
  String validEmail;
  String validPassword;
  String validNickname;

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
  class 팔로잉_생성_테스트 {

    @ParameterizedTest(name = "생성 시 상태: {0}")
    @NullSource
    @ValueSource(strings = "REQUESTED")
    void 팔로잉_생성을_성공한다(String statusName) {
      // given
      User followee = createUser(null);
      String followerEmail = faker.internet()
          .emailAddress();
      User follower = createUser(followerEmail);
      FollowingStatus status = Objects.nonNull(statusName) ? FollowingStatus.valueOf(statusName)
          : null;

      // when
      Following following = Following.builder()
          .follower(follower)
          .followee(followee)
          .status(status)
          .build();

      // then
      User actualFollower = following.getFollower();
      User actualFollowee = following.getFollowee();

      assertThat(actualFollower.getId()).isEqualTo(follower.getId());
      assertThat(actualFollowee.getId()).isEqualTo(followee.getId());
    }

    @ParameterizedTest
    @NullSource
    void 팔로워가_NULL이면_팔로잉_생성을_실패한다(User follower) {
      // given
      User followee = createUser(null);

      // when
      ThrowingCallable construct = () -> Following.builder()
          .followee(followee)
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
      User follower = createUser(null);

      // when
      ThrowingCallable construct = () -> Following.builder()
          .follower(follower)
          .followee(followee)
          .build();

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct)
          .withMessage(FollowingErrorCode.NULL_FOLLOWEE.getMessage());
    }

    @Test
    void 본인을_팔로우하는_팔로잉_생성을_실패한다() {
      // given
      User follower = createUser(null);

      // when
      ThrowingCallable construct = () -> Following.builder()
          .follower(follower)
          .followee(follower)
          .build();

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(construct)
          .withMessage(FollowingErrorCode.SELF_FOLLOWING_UNAVAILABLE.getMessage());
    }

  }

  @Nested
  class 팔로잉_상태_수정_테스트 {

    User follower;
    User followee;

    @BeforeEach
    void setUp() {
      followee = createUser(null);
      String followerEmail = faker.internet()
          .emailAddress();
      follower = createUser(followerEmail);
    }

    @Test
    void 팔로잉_요청을_수락한다() {
      // given
      Following following = Following.builder()
          .follower(follower)
          .followee(followee)
          .status(FollowingStatus.REQUESTED)
          .build();
      FollowingStatus expected = FollowingStatus.FOLLOWING;

      // when
      following.updateStatus(expected);

      // then
      assertThat(following.getStatus()).isEqualTo(expected);
    }

    @Test
    void 팔로잉_요청을_무시한다() {
      // given
      Following following = Following.builder()
          .follower(follower)
          .followee(followee)
          .status(FollowingStatus.REQUESTED)
          .build();
      FollowingStatus expected = FollowingStatus.IGNORED;

      // when
      following.updateStatus(expected);

      // then

      assertThat(following.getStatus()).isEqualTo(expected);
    }

    @Test
    void 수정할_상태가_Null이면_수정을_실패한다() {
      // given
      Following following = Following.builder()
          .follower(follower)
          .followee(followee)
          .build();
      FollowingStatus status = null;

      // when
      ThrowingCallable updateStatus = () -> following.updateStatus(status);

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(updateStatus)
          .withMessage(FollowingErrorCode.NULL_STATUS_REQUESTED.getMessage());
    }

    @Test
    void 수정할_상태가_REQUESTED이면_수정을_실패한다() {
      // given
      Following following = Following.builder()
          .follower(follower)
          .followee(followee)
          .build();
      FollowingStatus status = FollowingStatus.REQUESTED;

      // when
      ThrowingCallable updateStatus = () -> following.updateStatus(status);

      // then
      assertThatExceptionOfType(InvalidParameterException.class).isThrownBy(updateStatus)
          .withMessage(FollowingErrorCode.REQUEST_UNAVAILABLE.getMessage());
    }

  }

  private User createUser(String email) {
    return builderWithEncoder
        .email(Objects.isNull(email) ? validEmail : email)
        .password(validPassword)
        .nickname(validNickname)
        .build();
  }

}
