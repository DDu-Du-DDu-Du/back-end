package com.ddudu.todo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.presentation.api.exception.InvalidParameterException;
import com.ddudu.old.goal.domain.Goal;
import com.ddudu.old.todo.domain.Todo;
import com.ddudu.old.todo.domain.TodoStatus;
import com.ddudu.old.todo.exception.TodoErrorCode;
import com.ddudu.old.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TodoTest {

  static final Faker faker = new Faker();

  User user;
  Goal goal;

  @BeforeEach
  void setUp() {
    user = createUser();
    goal = createGoal("dev course", user);
  }

  @Nested
  class 할_일_생성_테스트 {

    @Test
    void 할_일을_생성할_수_있다() {
      // given
      String name = "Todo 엔티티 테스트 코드 짜기";

      // when
      Todo todo = Todo.builder()
          .name(name)
          .goal(goal)
          .user(user)
          .build();

      // then
      assertThat(todo)
          .extracting("goal", "user", "name", "status")
          .containsExactly(goal, user, name, TodoStatus.UNCOMPLETED);
      assertThat(todo).extracting("beginAt")
          .isNotNull();
      assertThat(todo).extracting("endAt")
          .isNull();
    }

    @Test
    void 할_일_시작_날짜와_함께_할_일을_생성할_수_있다() {
      // given
      String name = "Todo 엔티티 테스트 코드 짜기";
      LocalDateTime beginAt = LocalDateTime.of(2023, 12, 25, 0, 0);

      // when
      Todo todo = Todo.builder()
          .name(name)
          .goal(goal)
          .user(user)
          .beginAt(beginAt)
          .build();

      // then
      assertThat(todo)
          .extracting("goal", "user", "name", "status")
          .containsExactly(goal, user, name, TodoStatus.UNCOMPLETED);
      assertThat(todo).extracting("endAt")
          .isNull();
    }

    @Test
    void 목표_없이는_할_일을_생성할_수_없다() {
      // when then
      assertThatThrownBy(() -> Todo.builder()
          .name("Todo 엔티티 테스트 코드 짜기")
          .build())
          .isInstanceOf(InvalidParameterException.class)
          .hasMessage(TodoErrorCode.NULL_GOAL_VALUE.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 할_일은_필수값이며_빈_문자열일_수_없다(String invalidName) {
      // when then
      assertThatThrownBy(() -> Todo.builder()
          .name(invalidName)
          .goal(goal)
          .user(user)
          .build())
          .isInstanceOf(InvalidParameterException.class)
          .hasMessage(TodoErrorCode.BLANK_NAME.getMessage());
    }

    @ParameterizedTest(name = "{index}. {0}은 50자를 초과한다.")
    @MethodSource("provideLongString")
    void 할_일_생성_시_할_일의_내용은_50자를_초과할_수_없다(String longName) {
      // when then
      assertThatThrownBy(() -> Todo.builder()
          .name(longName)
          .goal(goal)
          .user(user)
          .build())
          .isInstanceOf(InvalidParameterException.class)
          .hasMessage(TodoErrorCode.EXCESSIVE_NAME_LENGTH.getMessage());
    }

    private static List<String> provideLongString() {
      String longString = "a".repeat(100);
      return List.of(longString);
    }

  }

  private Goal createGoal(String name, User user) {
    return Goal.builder()
        .name(name)
        .user(user)
        .build();
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

}
