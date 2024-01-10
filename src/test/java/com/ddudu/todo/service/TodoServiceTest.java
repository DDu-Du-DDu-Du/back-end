package com.ddudu.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.following.dto.request.FollowRequest;
import com.ddudu.following.service.FollowingService;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.domain.PrivacyType;
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
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class TodoServiceTest {

  static final Faker faker = new Faker();

  @Autowired
  TodoService todoService;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  GoalRepository goalRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  FollowingService followingService;

  @Autowired
  EntityManager entityManager;

  @Autowired
  JwtEncoder jwtEncoder;

  User loginUser;
  User user;
  LocalDateTime beginAt;
  String name;
  String goalName;

  @BeforeEach
  void setUp() {
    loginUser = createUser();
    user = createUser();
    beginAt = LocalDateTime.now();
    name = faker.lorem()
        .word();
    goalName = faker.lorem()
        .word();
  }

  @Nested
  class 할_일_생성_테스트 {

    @Test
    void 할_일_생성에_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      CreateTodoRequest request = new CreateTodoRequest(goal.getId(), name, beginAt);

      // when
      TodoInfo response = todoService.create(user.getId(), request);

      // then
      Todo actual = todoRepository.findById(response.id())
          .get();
      assertThat(actual).extracting("name", "beginAt", "goal", "user")
          .containsExactly(name, beginAt, goal, user);
    }

    @Test
    void 사용자ID가_유효하지_않으면_할_일_생성을_실패한다() {
      // give
      Long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Goal goal = createGoal(goalName, user);
      CreateTodoRequest request = new CreateTodoRequest(goal.getId(), name, beginAt);

      // when
      ThrowingCallable create = () -> todoService.create(userId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(TodoErrorCode.USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 목표ID가_유효하지_않으면_할_일_생성을_실패한다() {
      // given
      Long goalId = faker.random()
          .nextLong(Long.MAX_VALUE);
      CreateTodoRequest request = new CreateTodoRequest(goalId, name, beginAt);

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
      Goal goal = createGoal(goalName, user);
      Todo todo = createTodo(name, goal, user);

      // when
      TodoResponse response = todoService.findById(user.getId(), todo.getId());

      // then
      assertThat(response).extracting(
              "goalInfo.id", "goalInfo.name", "todoInfo.id", "todoInfo.name", "todoInfo.status")
          .containsExactly(
              goal.getId(), goal.getName(), todo.getId(), todo.getName(), todo.getStatus());
    }

    @Test
    void 아이디가_존재하지_않아_할_일_조회를_실패한다() {
      // given
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when then
      assertThatThrownBy(() -> todoService.findById(user.getId(), id))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 로그인_사용자_아이디와_할_일_사용자_아이디가_다르면_조회를_실패한다() {
      // given
      Long loginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Goal goal = createGoal(goalName, user);
      Todo todo = createTodo(name, goal, user);

      // when
      ThrowingCallable findById = () -> todoService.findById(loginId, todo.getId());

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(findById)
          .withMessage(TodoErrorCode.INVALID_AUTHORITY.getMessage());
    }

  }

  @Nested
  class 일별_할_일_조회_테스트 {

    @Test
    void 주어진_날짜에_자신의_할_일_리스트_조회를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Todo todo1 = createTodo(name, goal, user);
      Todo todo2 = createTodo("JPA N+1 문제 해결", goal, user);

      LocalDate date = LocalDate.now();

      // when
      List<TodoListResponse> responses = todoService.findAllByDate(
          user.getId(), user.getId(), date);

      // then
      assertThat(responses).hasSize(1);

      TodoListResponse response1 = responses.get(0);
      assertThat(response1.goalInfo()
          .name())
          .isEqualTo(goal.getName());
      assertThat(response1.todolist()).extracting("id")
          .containsExactly(todo1.getId(), todo2.getId());

    }

    @Test
    void 주어진_날짜에_팔로워의_할_일_리스트_조회를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Todo todo = createTodo(name, goal, user);

      LocalDate date = LocalDate.now();

      FollowRequest request = new FollowRequest(user.getId());
      followingService.create(loginUser.getId(), request);

      // when
      List<TodoListResponse> responses = todoService.findAllByDate(
          loginUser.getId(), user.getId(), date);

      // then
      assertThat(responses).hasSize(0);
      assertThat(goal.getPrivacyType()).isEqualTo(PrivacyType.PRIVATE);
    }

    @Test
    void 주어진_날짜에_다른_사용자의_할_일_리스트_조회를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Todo todo = createTodo(name, goal, user);

      LocalDate date = LocalDate.now();

      // when
      List<TodoListResponse> responses = todoService.findAllByDate(
          loginUser.getId(), user.getId(), date);

      // then
      assertThat(responses).hasSize(0);
      assertThat(goal.getPrivacyType()).isEqualTo(PrivacyType.PRIVATE);
    }

    @Test
    void 로그인_아이디가_존재하지_않아_일별_할_일_조회를_실패한다() {
      // given
      Long invalidLoginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LocalDate date = LocalDate.now();

      // when then
      assertThatThrownBy(() -> todoService.findAllByDate(invalidLoginId, user.getId(), date))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 사용자_아이디가_존재하지_않아_일별_할_일_조회를_실패한다() {
      // given
      Long invalidUserId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LocalDate date = LocalDate.now();

      // when then
      assertThatThrownBy(() -> todoService.findAllByDate(loginUser.getId(), invalidUserId, date))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.USER_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 할_일_상태_업데이트_테스트 {

    @Test
    void 할_일_상태_업데이트를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Todo todo = createTodo(name, goal, user);
      TodoStatus beforeUpdated = todo.getStatus();

      // when
      TodoResponse response = todoService.updateStatus(user.getId(), todo.getId());

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
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when then
      assertThatThrownBy(() -> todoService.updateStatus(user.getId(), id))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 로그인_사용자_아이디와_할_일_사용자_아이디가_다르면_상태_업데이트_실패한다() {
      // given
      Long loginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Goal goal = createGoal(goalName, user);
      Todo todo = createTodo(name, goal, user);

      // when
      ThrowingCallable updateStatus = () -> todoService.updateStatus(loginId, todo.getId());

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(updateStatus)
          .withMessage(TodoErrorCode.INVALID_AUTHORITY.getMessage());
    }

  }

  @Nested
  class 할_일_달성률_조회_테스트 {

    @Test
    void 주간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(goalName, user);
      Goal goal2 = createGoal("book", user);
      Todo todo1 = createTodo(name, goal1, user);
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
      Long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LocalDate date = LocalDate.now();

      // when then
      assertThatThrownBy(() -> todoService.findWeeklyCompletions(userId, date))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 월간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(goalName, user);
      Goal goal2 = createGoal("book", user);
      Todo todo1 = createTodo(name, goal1, user);
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
      Long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      YearMonth yearMonth = YearMonth.now();

      // when then
      assertThatThrownBy(() -> todoService.findMonthlyCompletions(userId, yearMonth))
          .isInstanceOf(DataNotFoundException.class)
          .hasMessage(TodoErrorCode.USER_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 할_일_삭제_테스트 {

    @Test
    void 할_일을_삭제를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Todo todo = createTodo(name, goal, user);

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
    void 로그인_사용자_아이디와_삭제할_할_일_사용자_아이디가_다르면_삭제를_실패한다() {
      // given
      Long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Goal goal = createGoal(goalName, user);
      Todo todo = createTodo(name, goal, user);

      Optional<Todo> found = todoRepository.findById(todo.getId());
      assertThat(found).isNotEmpty();

      // when
      ThrowingCallable delete = () -> todoService.delete(userId, todo.getId());

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
