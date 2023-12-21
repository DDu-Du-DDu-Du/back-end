package com.ddudu.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;

class GoalTest {

  @Nested
  @DisplayName("목표 생성 테스트")
  class ConstructorTest {

    @Test
    @DisplayName("목표를 생성할 수 있다.")
    void create() {
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
    @DisplayName("색상 코드, 보기 설정과 함께 목표를 생성할 수 있다.")
    void createWithColorAndPrivacyType() {
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

    @Test
    @DisplayName("목표명 없이는 목표를 생성할 수 없다.")
    void createWithoutName() {
      // when then
      assertThatThrownBy(() -> Goal.builder()
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("목표명은 필수값입니다.");
    }

    @ParameterizedTest(name = "{index}. {0}은 50자를 초과한다.")
    @DisplayName("목표 생성 시 목표명은 50자를 초과할 수 없다.")
    @MethodSource("provideLongString")
    void createWithLongName(String longName) {
      // when then
      assertThatThrownBy(() -> Goal.builder()
          .name(longName)
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("목표명은 최대 50자 입니다.");
    }

    @ParameterizedTest
    @DisplayName("목표 생성 시 색상 코드가 빈 문자열이면 기본값으로 저장된다.")
    @EmptySource
    void createWithBlankColor(String emptyColor) {
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
    @DisplayName("목표 생성 시 색상 코드는 6자리 16진수 포맷을 따라야 한다.")
    void createWithInvalidColor() {
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

    private static List<String> provideLongString() {
      String longString = "a".repeat(100);
      return List.of(longString);
    }

  }

}
