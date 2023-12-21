package com.ddudu.todo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.goal.domain.Goal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class TodoTest {

  @Nested
  @DisplayName("할 일 생성 테스트")
  class ConstructorTest {

    @Test
    @DisplayName("할 일을 생성할 수 있다.")
    void create() {
      // given
      String name = "Todo 엔티티 테스트 코드 짜기";
      Goal goal = createGoal("dev course");

      // when
      Todo todo = Todo.builder()
          .name(name)
          .goal(goal)
          .build();

      // then
      assertThat(todo)
          .extracting("goal", "name", "status", "isDeleted")
          .containsExactly(goal, name, TodoStatus.UNCOMPLETED, false);
      assertThat(todo).extracting("beginAt")
          .isNotNull();
      assertThat(todo).extracting("endAt")
          .isNull();
    }

    @Test
    @DisplayName("할 일 시작 날짜와 함께 할 일을 생성할 수 있다.")
    void createWithBeginAt() {
      // given
      String name = "Todo 엔티티 테스트 코드 짜기";
      Goal goal = createGoal("dev course");
      LocalDateTime beginAt = LocalDateTime.of(2023, 12, 25, 0, 0);

      // when
      Todo todo = Todo.builder()
          .name(name)
          .goal(goal)
          .beginAt(beginAt)
          .build();

      // then
      assertThat(todo)
          .extracting("goal", "name", "status", "beginAt", "isDeleted")
          .containsExactly(goal, name, TodoStatus.UNCOMPLETED, beginAt, false);
      assertThat(todo).extracting("endAt")
          .isNull();
    }

    @Test
    @DisplayName("목표 없이는 할 일을 생성할 수 없다.")
    void createWithoutGoal() {
      // when then
      assertThatThrownBy(() -> Todo.builder()
          .name("Todo 엔티티 테스트 코드 짜기")
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("목표는 필수값입니다.");
    }

    @ParameterizedTest
    @DisplayName("할 일(name)은 필수값이며 빈 문자열일 수 없다.")
    @NullAndEmptySource
    void createWithoutName(String invalidName) {
      // given
      Goal goal = createGoal("dev course");

      // when then
      assertThatThrownBy(() -> Todo.builder()
          .name(invalidName)
          .goal(goal)
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("할 일은 필수값입니다.");
    }

    @ParameterizedTest(name = "{index}. {0}은 50자를 초과한다.")
    @DisplayName("할 일 생성 시 할 일의 내용은 50자를 초과할 수 없다.")
    @MethodSource("provideLongString")
    void createWithLongName(String longName) {
      // given
      Goal goal = createGoal("dev course");

      // when then
      assertThatThrownBy(() -> Todo.builder()
          .name(longName)
          .goal(goal)
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("할 일은 최대 50자 입니다.");
    }

    private static Goal createGoal(String name) {
      return Goal.builder()
          .name(name)
          .build();
    }

    private static List<String> provideLongString() {
      String longString = "a".repeat(100);
      return List.of(longString);
    }

  }

}
