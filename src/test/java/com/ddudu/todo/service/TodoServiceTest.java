package com.ddudu.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.auth.domain.authority.Authority;
import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.domain.TodoStatus;
import com.ddudu.todo.dto.request.CreateTodoRequest;
import com.ddudu.todo.dto.response.TodoCompletionResponse;
import com.ddudu.todo.dto.response.TodoInfo;
import com.ddudu.todo.dto.response.TodoListResponse;
import com.ddudu.todo.dto.response.TodoResponse;
import com.ddudu.todo.exception.TodoErrorCode;
import com.ddudu.todo.repository.TodoRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class TodoServiceTest {

  static final Faker faker = new Faker();
  static final JwsHeader header = JwsHeader.with(MacAlgorithm.HS512)
      .build();
  static final JwtClaimsSet.Builder claimSet = JwtClaimsSet.builder()
      .claim("auth", Authority.NORMAL);

  @Autowired
  TodoService todoService;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  GoalRepository goalRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  EntityManager entityManager;

  @Autowired
  JwtEncoder jwtEncoder;

  User user;
  LocalDateTime beginAt;
  String validName;
  String validGoalName;

  @BeforeEach
  void setUp() {
    user = createUser();
    beginAt = LocalDateTime.now();
    validName = faker.lorem()
        .word();
    validGoalName = faker.lorem()
        .word();
  }

  @Nested
  class 할_일_생성_테스트 {

    @Test
    void 할_일_생성에_성공한다() {
      // given
      Goal goal = createGoal(validGoalName, user);
      CreateTodoRequest request = new CreateTodoRequest(goal.getId(), validName, beginAt);

      // when
      TodoInfo response = todoService.create(user.getId(), request);

      // then
      Todo actual = todoRepository.findById(response.id())
          .get();
      assertThat(actual).extracting("name", "beginAt", "goal", "user")
          .containsExactly(validName, beginAt, goal, user);
    }

    @Test
    void 사용자ID가_유효하지_않으면_예외가_발생한다() {
      // give
      Long userRandomId = faker.random()
          .nextLong();
      Goal goal = createGoal(validGoalName, user);
      CreateTodoRequest request = new CreateTodoRequest(goal.getId(), validName, beginAt);

      // when
      ThrowingCallable create = () -> todoService.create(userRandomId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(TodoErrorCode.USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 목표ID가_유효하지_않으면_예외가_발생한다() {
      // given
      Long goalRandomId = faker.random()
          .nextLong();
      CreateTodoRequest request = new CreateTodoRequest(goalRandomId, validName, beginAt);

      // when
      ThrowingCallable create = () -> todoService.create(user.getId(), request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(TodoErrorCode.GOAL_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 할_일_1개_조회_테스트 {

    @Test
    void 할_일_조회를_성공한다() {
      // given
      Goal goal = createGoal(validGoalName, user);
      Todo todo = createTodo(validName, goal, user);

      // when
      TodoResponse response = todoService.findById(todo.getId());

      // then
      assertThat(response).extracting(
              "goalInfo.id", "goalInfo.name", "todoInfo.id", "todoInfo.name", "todoInfo.status")
          .containsExactly(
              goal.getId(), goal.getName(), todo.getId(), todo.getName(), todo.getStatus());
    }

    @Test
    void 아이디가_존재하지_않아_할_일_조회를_실패한다() {
      // given
      Long randomId = faker.random()
          .nextLong();

      // when then
      assertThatThrownBy(() -> todoService.findById(randomId))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.ID_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 일별_할_일_조회_테스트 {

    @Test
    void 주어진_날짜에_할_일_리스트_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(validGoalName, user);
      Goal goal2 = createGoal("book", user);
      Todo todo1 = createTodo(validName, goal1, user);
      Todo todo2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();

      // when
      List<TodoListResponse> responses = todoService.findAllByDate(user.getId(), date);

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
      Long userRandomId = faker.random()
          .nextLong();
      LocalDate date = LocalDate.now();

      // when then
      assertThatThrownBy(() -> todoService.findAllByDate(userRandomId, date))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.USER_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 할_일_상태_업데이트_테스트 {

    @Test
    void 할_일_상태_업데이트를_성공한다() {
      // given
      Goal goal = createGoal(validGoalName, user);
      Todo todo = createTodo(validName, goal, user);
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
      Long randomId = faker.random()
          .nextLong();

      // when then
      assertThatThrownBy(() -> todoService.updateStatus(randomId))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.ID_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 할_일_달성률_조회_테스트 {

    @Test
    void 주간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(validGoalName, user);
      Goal goal2 = createGoal("book", user);
      Todo todo1 = createTodo(validName, goal1, user);
      Todo todo2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();
      LocalDate mondayDate = date.with(DayOfWeek.MONDAY);
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      int dayIndex = dayOfWeek.getValue() - 1;

      // when
      List<TodoCompletionResponse> responses = todoService.findWeeklyCompletions(
          user.getId(), mondayDate);

      // then
      assertThat(responses).hasSize(7);
      assertThat(responses.get(dayIndex)).extracting("date", "totalTodos", "uncompletedTodos")
          .containsExactly(date, 2, 2);
    }

    @Test
    void 사용자_아이디가_존재하지_않아_주간_할_일_달성률_조회를_실패한다() {
      // given
      Long userRandomId = faker.random()
          .nextLong();
      LocalDate date = LocalDate.now();

      // when then
      assertThatThrownBy(() -> todoService.findWeeklyCompletions(userRandomId, date))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 월간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(validGoalName, user);
      Goal goal2 = createGoal("book", user);
      Todo todo1 = createTodo(validName, goal1, user);
      Todo todo2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();
      YearMonth yearMonth = YearMonth.now();
      int daysInMonth = yearMonth.lengthOfMonth();
      int dayOfMonthIndex = date.getDayOfMonth() - 1;

      // when
      List<TodoCompletionResponse> responses = todoService.findMonthlyCompletions(
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
      Long userRandomId = faker.random()
          .nextLong();
      YearMonth yearMonth = YearMonth.now();

      // when then
      assertThatThrownBy(() -> todoService.findMonthlyCompletions(userRandomId, yearMonth))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.USER_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 할_일_삭제_테스트 {

    @Test
    void 할_일을_삭제_할_수_있다() {
      // given
      Goal goal = createGoal(validGoalName, user);
      Todo todo = createTodo(validName, goal, user);

      Optional<Todo> found = todoRepository.findById(todo.getId());
      assertThat(found).isNotEmpty();

      // when
      todoService.delete(user.getId(), todo.getId());
      flushAndClearPersistence();

      // then
      Optional<Todo> foundAfterDeleted = todoRepository.findById(todo.getId());
      assertThat(foundAfterDeleted).isEmpty();
    }

    @Test
    void 로그인_사용자_아이디와_삭제할_할_일_사용자_아이디가_다르면_삭제할_수_없다() {
      // given
      Long randomId = faker.random()
          .nextLong();
      Goal goal = createGoal(validGoalName, user);
      Todo todo = createTodo(validName, goal, user);

      Optional<Todo> found = todoRepository.findById(todo.getId());
      assertThat(found).isNotEmpty();

      // when
      ThrowingCallable delete = () -> todoService.delete(randomId, todo.getId());

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(delete)
          .withMessage(TodoErrorCode.INVALID_AUTHORITY.getMessage());
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

  private void flushAndClearPersistence() {
    entityManager.flush();
    entityManager.clear();
  }

}
