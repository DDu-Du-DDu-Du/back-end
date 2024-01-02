package com.ddudu.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.domain.TodoStatus;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.repository.TodoRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class TodoServiceTest {

  static final Faker faker = new Faker();
  User user;

  @Autowired
  TodoService todoService;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  GoalRepository goalRepository;

  @Autowired
  UserRepository userRepository;

  @BeforeEach
  void setUp() {
    user = createUser();
  }

  @Nested
  class 할_일_1개_조회_테스트 {

    @Test
    void 할_일_조회를_성공한다() {
      // given
      Goal goal = createGoal("dev course", user);
      Todo todo = createTodo("할 일 1개 조회 기능 구현", goal, user);

      // when
      TodoResponse response = todoService.findById(todo.getId());

      // then
      assertThat(response).extracting(
              "goalInfo.id", "goalInfo.name", "todoInfo.id", "todoInfo.name", "todoInfo.status")
          .containsExactly(goal.getId(), goal.getName(), todo.getId(), todo.getName(),
              todo.getStatus()
          );
    }

    @Test
    void 아이디가_존재하지_않아_할_일_조회를_실패한다() {
      // given
      Long invalidId = 999L;

      // when then
      assertThatThrownBy(() -> todoService.findById(invalidId))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessage("할 일 아이디가 존재하지 않습니다.");
    }

  }

  @Nested
  class 일별_할_일_조회_테스트 {

    @Test
    void 주어진_날짜에_할_일_리스트_조회를_성공한다() {
      // given
      Goal goal1 = createGoal("dev course", user);
      Goal goal2 = createGoal("book", user);
      Todo todo1 = createTodo("할 일 1개 조회 기능 구현", goal1, user);
      Todo todo2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();

      // when
      List<TodoListResponse> responses = todoService.findDailyTodoList(user.getId(), date);

      // then
      assertThat(responses).hasSize(2);

      TodoListResponse response1 = responses.get(0);
      assertThat(response1.goalInfo()
          .id()).isEqualTo(goal1.getId());
      assertThat(response1.todolist()).extracting("id")
          .containsExactly(todo1.getId(), todo2.getId());

      TodoListResponse response2 = responses.get(1);
      assertThat(response2.goalInfo()
          .id()).isEqualTo(goal2.getId());
      assertThat(response2.todolist()).isEmpty();

    }

    @Test
    void 사용자_아이디가_존재하지_않아_일별_할_일_조회를_실패한다() {
      // given
      Long invalidUserId = 999L;
      LocalDate date = LocalDate.now();

      // when then
      assertThatThrownBy(() -> todoService.findDailyTodoList(invalidUserId, date))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessage("사용자가 존재하지 않습니다.");
    }

  }

  @Nested
  class 할_일_상태_업데이트_테스트 {

    @Test
    void 할_일_상태_업데이트를_성공한다() {
      // given
      Goal goal = createGoal("dev course", user);
      Todo todo = createTodo("할 일 1개 조회 기능 구현", goal, user);
      TodoStatus beforeUpdated = todo.getStatus();

      // when
      TodoResponse response = todoService.updateStatus(todo.getId());

      // then
      assertThat(response).extracting(
              "goalInfo.id", "goalInfo.name", "todoInfo.id", "todoInfo.name")
          .containsExactly(goal.getId(), goal.getName(), todo.getId(), todo.getName());
      assertThat(response.todoInfo()
          .status()).isNotEqualTo(beforeUpdated);
    }

    @Test
    void 아이디가_존재하지_않아_할_일_상태_업데이트를_실패한다() {
      // given
      Long invalidId = 999L;

      // when then
      assertThatThrownBy(() -> todoService.updateStatus(invalidId))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessage("할 일 아이디가 존재하지 않습니다.");
    }

  }

  @Nested
  class 할_일_달성률_조회_테스트 {

    @Test
    void 주간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal("dev course", user);
      Goal goal2 = createGoal("book", user);
      Todo todo1 = createTodo("할 일 1개 조회 기능 구현", goal1, user);
      Todo todo2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();
      LocalDate mondayDate = date.with(DayOfWeek.MONDAY);
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      int dayIndex = dayOfWeek.getValue() - 1;

      // when
      List<TodoCompletionResponse> responses = todoService.findWeeklyTodoCompletion(
          user.getId(), mondayDate);

      // then
      assertThat(responses).hasSize(7);

      assertThat(responses.get(dayIndex)).extracting("date", "totalTodos", "uncompletedTodos")
          .containsExactly(date, 2, 2);
    }

    @Test
    void 사용자_아이디가_존재하지_않아_주간_할_일_달성률_조회를_실패한다() {
      // given
      Long invalidUserId = 999L;
      LocalDate date = LocalDate.now();

      // when then
      assertThatThrownBy(() -> todoService.findWeeklyTodoCompletion(invalidUserId, date))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessage("사용자가 존재하지 않습니다.");
    }

    @Test
    void 월간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal("dev course", user);
      Goal goal2 = createGoal("book", user);
      Todo todo1 = createTodo("할 일 1개 조회 기능 구현", goal1, user);
      Todo todo2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();
      YearMonth yearMonth = YearMonth.now();
      int daysInMonth = yearMonth.lengthOfMonth();
      int dayOfMonthIndex = date.getDayOfMonth() - 1;

      // when
      List<TodoCompletionResponse> responses = todoService.findMonthlyTodoCompletion(
          user.getId(), yearMonth);

      // then
      assertThat(responses).hasSize(daysInMonth);

      assertThat(responses.get(dayOfMonthIndex)).extracting(
              "date", "totalTodos", "uncompletedTodos")
          .containsExactly(date, 2, 2);
    }

    @Test
    void 사용자_아이디가_존재하지_않아_월간_할_일_달성률_조회를_실패한다() {
      // given
      Long invalidUserId = 999L;
      YearMonth yearMonth = YearMonth.now();

      // when then
      assertThatThrownBy(() -> todoService.findMonthlyTodoCompletion(invalidUserId, yearMonth))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessage("사용자가 존재하지 않습니다.");
    }

  }

  private Goal createGoal(String name, User user) {
    Goal goal = Goal.builder()
        .name(name)
        .user(user)
        .build();

    return goalRepository.save(goal);
  }

  private Todo createTodo(String name, Goal goal, User user) {
    Todo todo = Todo.builder()
        .name(name)
        .goal(goal)
        .user(user)
        .build();

    return todoRepository.save(todo);
  }

  private User createUser() {
    String email = faker.internet()
        .emailAddress();
    String password = faker.internet()
        .password(8, 40, true, true, true);
    String nickname = faker.oscarMovie()
        .character();

    User user = User.builder()
        .passwordEncoder(new BCryptPasswordEncoder())
        .email(email)
        .password(password)
        .nickname(nickname)
        .build();

    return userRepository.save(user);
  }

}
