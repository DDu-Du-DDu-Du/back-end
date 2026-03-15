package com.ddudu.domain.user.user.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.common.dto.Authority;
import com.ddudu.common.exception.UserErrorCode;
import com.ddudu.domain.user.user.aggregate.User.UserBuilder;
import com.ddudu.domain.user.user.aggregate.enums.RandomUserAdjective;
import com.ddudu.domain.user.user.aggregate.enums.RandomUserAnimal;
import com.ddudu.fixture.UserFixture;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class UserTest {

  @Nested
  class 생성_테스트 {

    String username;
    String nickname;
    String introduction;

    @BeforeEach
    void setUp() {
      String lowTime = UUID.randomUUID()
          .toString()
          .substring(0, 8);
      RandomUserAdjective adjective = RandomUserAdjective.getRandom();
      RandomUserAnimal animal = RandomUserAnimal.getRandom();
      username = adjective.getUsername() + animal.getUsername() + lowTime;
      nickname = adjective.getNickname() + " " + animal.getNickname();
      introduction = UserFixture.getRandomSentenceWithMax(45);
    }

    @Test
    void 유저_생성을_성공한다() {
      // given
      UserBuilder builder = User.builder()
          .username(username)
          .nickname(nickname)
          .introduction(introduction);

      // when
      ThrowingCallable build = builder::build;

      // then
      Assertions.assertThatNoException()
          .isThrownBy(build);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 닉네임이_빈_값이면_생성을_실패한다(String blankNickname) {
      // given
      UserBuilder builder = User.builder()
          .username(username)
          .nickname(blankNickname);

      // when
      ThrowingCallable build = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(build)
          .withMessage(UserErrorCode.BLANK_NICKNAME.getCodeName());
    }

    @Test
    void 닉네임이_20자보다_길면_생성을_실패한다() {
      // given
      String over20 = UserFixture.getRandomFixedSentence(21);
      UserBuilder builder = User.builder()
          .username(username)
          .nickname(over20);

      // when
      ThrowingCallable build = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(build)
          .withMessage(UserErrorCode.EXCESSIVE_NICKNAME_LENGTH.getCodeName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 사용자_아이디가_빈_값이면_생성을_실패한다(String blankUsername) {
      // given
      UserBuilder builder = User.builder()
          .username(blankUsername)
          .nickname(nickname);

      // when
      ThrowingCallable build = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(build)
          .withMessage(UserErrorCode.BLANK_USERNAME.getCodeName());
    }

    @Test
    void 사용자_아이디가_30자를_넘으면_생성을_실패한다() {
      // given
      String over30 = UserFixture.getRandomFixedSentence(31);
      UserBuilder builder = User.builder()
          .username(over30)
          .nickname(nickname);

      // when
      ThrowingCallable build = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(build)
          .withMessage(UserErrorCode.EXCESSIVE_USERNAME_LENGTH.getCodeName());
    }

    @Test
    void 자기소개가_50자를_넘으면_생성을_실패한다() {
      // given
      String over50 = UserFixture.getRandomFixedSentence(51);
      UserBuilder builder = User.builder()
          .username(username)
          .nickname(nickname)
          .introduction(over50);

      // when
      ThrowingCallable build = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(build)
          .withMessage(UserErrorCode.EXCESSIVE_INTRODUCTION_LENGTH.getCodeName());
    }

    @Test
    void 프로필_사진_링크가_1024자를_넘으면_생성을_실패한다() {
      // given
      String over1024 = UserFixture.getRandomFixedSentence(1025);
      UserBuilder builder = User.builder()
          .username(username)
          .nickname(nickname)
          .profileImageUrl(over1024);

      // when
      ThrowingCallable build = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(build)
          .withMessage(UserErrorCode.EXCESSIVE_PROFILE_IMAGE_URL_LENGTH.getCodeName());
    }

  }

  @Nested
  class AdminTest {

    @Test
    void authority가_ADMIN이면_true() {
      // given
      User user = UserFixture.createRandomUserWithAuthority(1L, Authority.ADMIN);

      // when
      boolean actual = user.isAdmin();

      // then
      assertThat(actual).isTrue();
    }

    @Test
    void authority가_ADMIN이_아니면_false() {
      // given
      User user = UserFixture.createRandomUserWithAuthority(1L, Authority.NORMAL);

      // when
      boolean actual = user.isAdmin();

      // then
      assertThat(actual).isFalse();
    }

  }

  @Nested
  class 기본_옵션_생성_테스트 {

    @Test
    void options가_null이면_신규_기본_옵션까지_함께_생성된다() {
      // given
      User user = UserFixture.createRandomUserWithNullOptions(1L);

      // when

      // then
      assertThat(user.isAllowingFollowsAfterApproval()).isFalse();
      assertThat(user.isNotifyingTemplate()).isTrue();
      assertThat(user.isNotifyingDdudu()).isTrue();
      assertThat(user.getWeekStartDay().name()).isEqualTo("SUN");
      assertThat(user.isDarkMode()).isFalse();
      assertThat(user.isActiveCalendar()).isTrue();
      assertThat(user.getPriorityCalendar()).isEqualTo(1);
      assertThat(user.isActiveDashboard()).isTrue();
      assertThat(user.getPriorityDashboard()).isEqualTo(2);
      assertThat(user.isActiveStats()).isTrue();
      assertThat(user.getPriorityStats()).isEqualTo(3);
      assertThat(user.isRealtimeSyncNotion()).isFalse();
      assertThat(user.isRealtimeSyncGoogleCalendar()).isFalse();
      assertThat(user.isRealtimeSyncMicrosoftTodo()).isFalse();
    }

  }

}
