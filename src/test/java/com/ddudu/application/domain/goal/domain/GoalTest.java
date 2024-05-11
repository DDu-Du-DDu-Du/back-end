package com.ddudu.application.domain.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.fixture.BaseFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.util.List;
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

@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalTest {

  User user;
  String name;
  String color;
  PrivacyType privacyType;

  @BeforeEach
  void setUp() {
    user = UserFixture.createRandomUserWithId();
    name = BaseFixture.getRandomSentenceWithMax(50);
    color = GoalFixture.getRandomColor();
    privacyType = GoalFixture.getRandomPrivacyType();
  }

  @Nested
  class 목표_생성_테스트 {

    @Test
    void 목표를_생성할_수_있다() {
      // when
      Goal goal = Goal.builder()
          .name(name)
          .user(user)
          .build();

      // then
      assertThat(goal)
          .extracting("name", "user", "status", "color", "privacyType")
          .containsExactly(
              name, user, GoalStatus.IN_PROGRESS, "191919", PrivacyType.PRIVATE);
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
          .extracting("name", "user", "status", "color", "privacyType")
          .containsExactly(name, user, GoalStatus.IN_PROGRESS, color, privacyType);
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
          .hasMessage(GoalErrorCode.NULL_USER.getCodeName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 목표명은_필수값이며_빈_문자열일_수_없다(String invalidName) {
      // when then
      assertThatThrownBy(() -> Goal.builder()
          .name(invalidName)
          .user(user)
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage(GoalErrorCode.BLANK_NAME.getCodeName());
    }

    @ParameterizedTest(name = "{index}. {0}은 50자를 초과한다.")
    @MethodSource("getLongerThan50Characters")
    void 목표명은_50자를_초과할_수_없다(String longName) {
      // when then
      assertThatThrownBy(() -> Goal.builder()
          .name(longName)
          .user(user)
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage(GoalErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
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
      String invalidColor = GoalFixture.getRandomFixedSentence(6);

      // when then
      assertThatThrownBy(() ->
          Goal.builder()
              .name(name)
              .user(user)
              .color(invalidColor)
              .build()
      )
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage(GoalErrorCode.INVALID_COLOR_FORMAT.getCodeName());

    }

    private static List<String> getLongerThan50Characters() {
      return List.of(BaseFixture.getRandomSentence(50, 100));
    }

  }

  @Nested
  class 목표_수정_테스트 {

    @Test
    void 목표명_색상_상태_공개_설정을_수정_할_수_있다() {
      // given
      Goal goal = GoalFixture.createRandomGoal();
      String changedName = GoalFixture.getRandomSentenceWithMax(50);
      String changedColor = GoalFixture.getRandomColor();
      GoalStatus changedStatus = GoalFixture.getRandomGoalStatus();
      PrivacyType changedPrivacyType = GoalFixture.getRandomPrivacyType();

      // when
      goal.applyGoalUpdates(changedName, changedStatus, changedColor, changedPrivacyType);

      // then
      assertThat(goal).extracting("name", "status", "color", "privacyType")
          .containsExactly(changedName, changedStatus, changedColor, changedPrivacyType);
    }

  }

}
