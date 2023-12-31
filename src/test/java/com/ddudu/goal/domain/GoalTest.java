package com.ddudu.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.goal.exception.GoalErrorCode;
import com.ddudu.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalTest {

  static final Faker faker = new Faker();

  User user;
  String name;
  String color;
  PrivacyType privacyType;

  @BeforeEach
  void setUp() {
    user = createUser();
    name = faker.lorem()
        .word();
    color = faker.color()
        .hex()
        .substring(1);
    privacyType = PrivacyType.PUBLIC;
  }

  @Nested
  class 목표_생성_테스트 {

    private static List<String> provide51Letters() {
      String longString = "a".repeat(51);
      return List.of(longString);
    }

    @Test
    void 목표를_생성할_수_있다() {
      // when
      Goal goal = Goal.builder()
          .name(name)
          .user(user)
          .build();

      // then
      assertThat(goal)
          .extracting("name", "user", "status", "color", "privacyType", "isDeleted")
          .containsExactly(
              name, user, GoalStatus.IN_PROGRESS, "191919", PrivacyType.PRIVATE, false);
    }

    @Test
    void 색상_코드_보기_설정과_함께_목표를_생성할_수_있다() {
      // when
      Goal goal = Goal.builder()
          .name(name)
          .user(user)
          .color(color)
          .privacyType(privacyType)
          .build();

      // then
      assertThat(goal)
          .extracting("name", "user", "status", "color", "privacyType", "isDeleted")
          .containsExactly(name, user, GoalStatus.IN_PROGRESS, color, privacyType, false);
    }

    @ParameterizedTest
    @NullSource
    void 사용자는_필수값이다(User invalidUser) {
      // when then
      assertThatThrownBy(() -> Goal.builder()
          .name(name)
          .user(invalidUser)
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("사용자는 필수값입니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 목표명은_필수값이며_빈_문자열일_수_없다(String invalidName) {
      // when then
      assertThatThrownBy(() -> Goal.builder()
          .name(invalidName)
          .user(user)
          .build())
          .isInstanceOf(InvalidParameterException.class)
          .hasMessage(GoalErrorCode.BLANK_NAME.getMessage());
    }

    @ParameterizedTest(name = "{index}. {0}은 50자를 초과한다.")
    @MethodSource("provide51Letters")
    void 목표명은_50자를_초과할_수_없다(String longName) {
      // when then
      assertThatThrownBy(() -> Goal.builder()
          .name(longName)
          .user(user)
          .build())
          .isInstanceOf(InvalidParameterException.class)
          .hasMessage(GoalErrorCode.EXCESSIVE_NAME_LENGTH.getMessage());
    }

    @ParameterizedTest
    @EmptySource
    void 색상_코드가_빈_문자열이면_기본값으로_저장된다(String emptyColor) {
      // when
      Goal goal = Goal.builder()
          .name(name)
          .user(user)
          .color(emptyColor)
          .build();

      // then
      assertThat(goal).extracting("color")
          .isEqualTo("191919");
    }

    @Test
    void 색상_코드는_6자리_16진수_포맷을_따라야_한다() {
      // given
      String invalidColor = "19191!";

      // when then
      assertThatThrownBy(() ->
          Goal.builder()
              .name(name)
              .user(user)
              .color(invalidColor)
              .build()
      )
          .isInstanceOf(InvalidParameterException.class)
          .hasMessage(GoalErrorCode.INVALID_COLOR_FORMAT.getMessage());

    }

  }

  @Nested
  class 목표_수정_테스트 {

    @Test
    void 목표명_색상_상태_공개_설정을_수정_할_수_있다() {
      // given
      Goal goal = createGoal();
      String changedName = "데브 코스";
      String changedColor = "999999";
      GoalStatus changedStatus = GoalStatus.DONE;
      PrivacyType changedPrivacyType = PrivacyType.PUBLIC;

      // when
      goal.applyGoalUpdates(changedName, changedStatus, changedColor, changedPrivacyType);

      // then
      assertThat(goal).extracting("name", "status", "color", "privacyType")
          .containsExactly(changedName, changedStatus, changedColor, changedPrivacyType);
    }

  }

  @Nested
  class 목표_삭제_테스트 {

    @Test
    void 목표를_삭제_상태로_변경할_수_있다() {
      // given
      Goal goal = createGoal();

      // when
      goal.delete();

      // then
      assertThat(goal.isDeleted()).isEqualTo(true);
    }

    @Test
    void 이미_삭제된_목표를_재삭제_하면_업데이트_시간이_변경되지_않는다() {
      // given
      Goal goal = createGoal();
      goal.delete();
      LocalDateTime beforeReDelete = goal.getUpdatedAt();

      // when
      goal.delete();

      // then
      assertThat(goal.isDeleted()).isEqualTo(true);
      assertThat(goal.getUpdatedAt()).isEqualTo(beforeReDelete);
    }

  }

  private User createUser() {
    String email = faker.internet()
        .emailAddress();
    String password = faker.internet()
        .password(8, 40, true, true, true);
    String nickname = faker.oscarMovie()
        .character();

    return User.builder()
        .passwordEncoder(new BCryptPasswordEncoder())
        .email(email)
        .password(password)
        .nickname(nickname)
        .build();
  }

  private Goal createGoal() {
    return Goal.builder()
        .name(name)
        .user(user)
        .color(color)
        .privacyType(PrivacyType.PRIVATE)
        .build();
  }

}
