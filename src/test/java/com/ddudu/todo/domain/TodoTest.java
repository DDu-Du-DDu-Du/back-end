package com.ddudu.todo.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.goal.domain.Goal;
import com.ddudu.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
  @DisplayName("할 일 생성 테스트")
  class ConstructorTest {

    @Test
    @DisplayName("할 일을 생성할 수 있다.")
    void create() {
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
          .extracting("goal", "user", "name", "status", "isDeleted")
          .containsExactly(goal, user, name, TodoStatus.UNCOMPLETED, false);
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
          .extracting("goal", "user", "name", "status", "isDeleted")
          .containsExactly(goal, user, name, TodoStatus.UNCOMPLETED, false);
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

      // when then
      assertThatThrownBy(() -> Todo.builder()
          .name(invalidName)
          .goal(goal)
          .user(user)
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("할 일은 필수값입니다.");
    }

    @ParameterizedTest(name = "{index}. {0}은 50자를 초과한다.")
    @DisplayName("할 일 생성 시 할 일의 내용은 50자를 초과할 수 없다.")
    @MethodSource("provideLongString")
    void createWithLongName(String longName) {
      // when then
      assertThatThrownBy(() -> Todo.builder()
          .name(longName)
          .goal(goal)
          .user(user)
          .build())
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessage("할 일은 최대 50자 입니다.");
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
