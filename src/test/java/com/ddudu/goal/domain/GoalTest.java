package com.ddudu.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalTest {

  @Nested
  class 목표_생성_테스트 {

    @Test
    void 목표를_생성할_수_있다() {
      // given
      String name = "dev course";

      // when
      Goal goal = Goal.builder()
          .name(name)
          .build();

      // then
      assertThat(goal)
          .extracting("name", "status", "color", "privacyType", "isDeleted")
          .containsExactly(name, GoalStatus.IN_PROGRESS, "191919", PrivacyType.PRIVATE, false);
    }

    @Test
    void 색상_코드_보기_설정과_함께_목표를_생성할_수_있다() {
      // given
      String name = "dev course";
      String color = "999999";
      PrivacyType privacyType = PrivacyType.PUBLIC;

      // when
      Goal goal = Goal.builder()
          .name(name)
          .color(color)
          .privacyType(privacyType)
          .build();

      // then
      assertThat(goal)
          .extracting("name", "status", "color", "privacyType", "isDeleted")
          .containsExactly(name, GoalStatus.IN_PROGRESS, color, privacyType, false);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 목표명은_필수값이며_빈_문자열일_수_없다(String invalidName) {
      // when then
      assertThatThrownBy(() -> Goal.builder()
          .name(invalidName)
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("목표명은 필수값입니다.");
    }

    @ParameterizedTest(name = "{index}. {0}은 50자를 초과한다.")
    @MethodSource("provide51Letters")
    void 목표명은_50자를_초과할_수_없다(String longName) {
      // when then
      assertThatThrownBy(() -> Goal.builder()
          .name(longName)
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("목표명은 최대 50자 입니다.");
    }

    @ParameterizedTest
    @EmptySource
    void 색상_코드가_빈_문자열이면_기본값으로_저장된다(String emptyColor) {
      // when
      Goal goal = Goal.builder()
          .name("dev course")
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
              .name("dev course")
              .color(invalidColor)
              .build()
      )
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("올바르지 않은 색상 코드입니다. 색상 코드는 6자리 16진수입니다.");

    }

    private static List<String> provide51Letters() {
      String longString = "a".repeat(51);
      return List.of(longString);
    }

  }

  @Nested
  class 목표_수정_테스트 {

    private String validName;
    private String validColor;

    목표_수정_테스트() {
      validName = "dav course";
      validColor = "191919";
    }

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
      assertThat(goal.getName()).isEqualTo(changedName);
      assertThat(goal).extracting("name", "status", "color", "privacyType")
          .containsExactly(changedName, changedStatus, changedColor, changedPrivacyType);
    }

    private Goal createGoal() {
      return Goal.builder()
          .name(validName)
          .color(validColor)
          .privacyType(PrivacyType.PRIVATE)
          .build();
    }

  }

}
