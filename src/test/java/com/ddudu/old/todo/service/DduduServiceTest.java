package com.ddudu.old.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.goal.domain.OldGoalRepository;
import com.ddudu.old.like.domain.Like;
import com.ddudu.old.like.domain.LikeRepository;
import com.ddudu.old.todo.domain.OldTodoRepository;
import com.ddudu.old.todo.dto.request.CreateTodoRequest;
import com.ddudu.old.todo.dto.request.UpdateTodoRequest;
import com.ddudu.old.todo.dto.response.TodoCompletionResponse;
import com.ddudu.old.todo.dto.response.TodoInfo;
import com.ddudu.old.todo.dto.response.TodoListResponse;
import com.ddudu.old.todo.dto.response.TodoResponse;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.old.user.dto.request.FollowRequest;
import com.ddudu.old.user.service.FollowingService;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.ForbiddenException;
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
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class DduduServiceTest {

  static final Faker faker = new Faker();

  @Autowired
  TodoService todoService;

  @Autowired
  OldTodoRepository oldTodoRepository;

  @Autowired
  OldGoalRepository oldGoalRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  FollowingService followingService;

  @Autowired
  LikeRepository likeRepository;

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
      Ddudu actual = oldTodoRepository.findById(response.id())
          .get();
      assertThat(actual).extracting("name", "beginAt", "goal", "user")
          .containsExactly(name, beginAt, goal, user);
    }

    @Test
    void 사용자_아이디가_유효하지_않으면_예외가_발생한다() {
      // give
      Long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Goal goal = createGoal(goalName, user);
      CreateTodoRequest request = new CreateTodoRequest(goal.getId(), name, beginAt);

      // when
      ThrowingCallable create = () -> todoService.create(userId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 목표_아이디가_유효하지_않으면_예외가_발생한다() {
      // given
      Long goalId = faker.random()
          .nextLong(Long.MAX_VALUE);
      CreateTodoRequest request = new CreateTodoRequest(goalId, name, beginAt);

      // when
      ThrowingCallable create = () -> todoService.create(user.getId(), request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(DduduErrorCode.GOAL_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 할_일_1개_조회_테스트 {

    @Test
    void 할_일_조회를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Ddudu ddudu = createTodo(name, goal, user);

      // when
      TodoResponse response = todoService.findById(user.getId(), ddudu.getId());

      // then
      assertThat(response).extracting(
              "goal.id", "goal.name", "todo.id", "todo.name", "todo.status")
          .containsExactly(
              goal.getId(), goal.getName(), ddudu.getId(), ddudu.getName(), ddudu.getStatus());
    }

    @Test
    void 아이디가_존재하지_않아_할_일_조회를_실패한다() {
      // given
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when
      ThrowingCallable findById = () -> todoService.findById(user.getId(), id);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(findById)
          .withMessage(DduduErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 로그인_사용자_아이디와_할_일_사용자_아이디가_다르면_조회를_실패한다() {
      // given
      Long loginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Goal goal = createGoal(goalName, user);
      Ddudu ddudu = createTodo(name, goal, user);

      // when
      ThrowingCallable findById = () -> todoService.findById(loginId, ddudu.getId());

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(findById)
          .withMessage(DduduErrorCode.INVALID_AUTHORITY.getMessage());
    }

  }

  @Nested
  class 일별_할_일_조회_테스트 {

    @Test
    void 주어진_날짜에_자신의_할_일_리스트_조회를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Ddudu ddudu1 = createTodo(name, goal, user);
      Ddudu ddudu2 = createTodo("JPA N+1 문제 해결", goal, user);

      LocalDate date = LocalDate.now();

      // when
      List<TodoListResponse> responses = todoService.findAllByDate(
          user.getId(), user.getId(), date);

      // then
      assertThat(responses).hasSize(1);

      TodoListResponse response1 = responses.get(0);
      assertThat(response1.goal()
          .name())
          .isEqualTo(goal.getName());
      assertThat(response1.todos()).extracting("id")
          .containsExactly(ddudu1.getId(), ddudu2.getId());

    }

    @Test
    void 주어진_날짜에_팔로워의_할_일_리스트_조회를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Ddudu ddudu = createTodo(name, goal, user);

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
      Ddudu ddudu = createTodo(name, goal, user);

      LocalDate date = LocalDate.now();

      // when
      List<TodoListResponse> responses = todoService.findAllByDate(
          loginUser.getId(), user.getId(), date);

      // then
      assertThat(responses).hasSize(0);
      assertThat(goal.getPrivacyType()).isEqualTo(PrivacyType.PRIVATE);
    }

    @Test
    void 주어진_날짜에_할_일_리스트_및_좋아요_조회를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Ddudu ddudu = createTodo(name, goal, user);
      ddudu.switchStatus();

      LocalDate date = LocalDate.now();

      User other = createUser();
      Like like = createLike(other, ddudu);

      // when
      List<TodoListResponse> responses = todoService.findAllByDate(
          user.getId(), user.getId(), date);

      // then
      assertThat(responses).hasSize(1);

      TodoListResponse response = responses.get(0);
      assertThat(response.todos()).hasSize(1);

      TodoInfo todoInfo = responses.get(0)
          .todos()
          .get(0);
      assertThat(todoInfo.likes()
          .count()).isEqualTo(1);
      assertThat(todoInfo.likes()
          .users()).containsExactly(other.getId());
    }

    @Test
    void 로그인_아이디가_존재하지_않아_일별_할_일_조회를_실패한다() {
      // given
      Long invalidLoginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LocalDate date = LocalDate.now();

      // when
      ThrowingCallable findAllByDate = () -> todoService.findAllByDate(
          invalidLoginId, user.getId(), date);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(findAllByDate)
          .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 사용자_아이디가_존재하지_않아_일별_할_일_조회를_실패한다() {
      // given
      Long invalidUserId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LocalDate date = LocalDate.now();

      // when
      ThrowingCallable findAllByDate = () -> todoService.findAllByDate(
          loginUser.getId(), invalidUserId, date);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(findAllByDate)
          .withMessage(DduduErrorCode.USER_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 할_일_수정_테스트 {

    Ddudu ddudu;
    Goal changedGoal;
    String changedName;
    LocalDateTime changedBeginAt;

    @BeforeEach
    void setUp() {
      Goal goal = createGoal(goalName, user);
      ddudu = createTodo(name, goal, user);

      String newGoalName = faker.lorem()
          .word();
      changedGoal = createGoal(newGoalName, user);

      changedName = faker.lorem()
          .word();
      changedBeginAt = LocalDateTime.now();
    }

    @Test
    void 할_일_수정에_성공한다() {
      // given
      UpdateTodoRequest request = new UpdateTodoRequest(
          changedGoal.getId(), changedName, changedBeginAt);

      // when
      TodoInfo response = todoService.update(user.getId(), ddudu.getId(), request);

      // then
      Optional<Ddudu> actual = oldTodoRepository.findById(ddudu.getId());
      assertThat(actual.get()).extracting(
              "goal", "name", "status")
          .containsExactly(changedGoal, response.name(), response.status());
    }

    @Test
    void 유효하지_않은_ID인_경우_수정에_실패한다() {
      // given
      Long invalidId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Long goalId = ddudu.getGoal()
          .getId();
      UpdateTodoRequest request = new UpdateTodoRequest(goalId, name, beginAt);

      // when
      ThrowingCallable update = () -> todoService.update(user.getId(), invalidId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(update)
          .withMessage(DduduErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 로그인_사용자가_권한이_없는_경우_수정에_실패한다() {
      // given
      Long invalidUserId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Long goalId = ddudu.getGoal()
          .getId();
      UpdateTodoRequest request = new UpdateTodoRequest(goalId, name, beginAt);

      // when
      ThrowingCallable update = () -> todoService.update(invalidUserId, ddudu.getId(), request);

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(update)
          .withMessage(DduduErrorCode.INVALID_AUTHORITY.getMessage());
    }

    @Test
    void 유효하지_않은_목표_ID인_경우_수정에_실패한다() {
      // given
      Long invalidGoalId = faker.random()
          .nextLong(Long.MAX_VALUE);
      UpdateTodoRequest request = new UpdateTodoRequest(invalidGoalId, name, beginAt);

      // when
      ThrowingCallable update = () -> todoService.update(user.getId(), ddudu.getId(), request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(update)
          .withMessage(DduduErrorCode.GOAL_NOT_EXISTING.getMessage());
    }

    @Test
    void 로그인_사용자가_목표에_대한_권한이_없는_경우_수정에_실패한다() {
      // given
      User anotherUser = createUser();
      Goal goalFromAnotherUser = createGoal(goalName, anotherUser);
      UpdateTodoRequest request = new UpdateTodoRequest(goalFromAnotherUser.getId(), name, beginAt);

      // when
      ThrowingCallable update = () -> todoService.update(user.getId(), ddudu.getId(), request);

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(update)
          .withMessage(DduduErrorCode.INVALID_AUTHORITY.getMessage());
    }

  }

  @Nested
  class 할_일_상태_업데이트_테스트 {

    @Test
    void 할_일_상태_업데이트를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Ddudu ddudu = createTodo(name, goal, user);
      DduduStatus beforeUpdated = ddudu.getStatus();

      // when
      todoService.updateStatus(user.getId(), ddudu.getId());

      // then
      Optional<Ddudu> actual = oldTodoRepository.findById(ddudu.getId());
      assertThat(actual.get()
          .getStatus()).isNotEqualTo(beforeUpdated);
    }

    @Test
    void 아이디가_존재하지_않아_할_일_상태_업데이트를_실패한다() {
      // given
      Long id = faker.random()
          .nextLong(Long.MAX_VALUE);

      // when
      ThrowingCallable updateStatus = () -> todoService.updateStatus(user.getId(), id);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(updateStatus)
          .withMessage(DduduErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 로그인_사용자_아이디와_할_일_사용자_아이디가_다르면_상태_업데이트_실패한다() {
      // given
      Long loginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Goal goal = createGoal(goalName, user);
      Ddudu ddudu = createTodo(name, goal, user);

      // when
      ThrowingCallable updateStatus = () -> todoService.updateStatus(loginId, ddudu.getId());

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(updateStatus)
          .withMessage(DduduErrorCode.INVALID_AUTHORITY.getMessage());
    }

  }

  @Nested
  class 할_일_달성률_조회_테스트 {

    @Test
    void 자신의_주간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(goalName, user);
      Goal goal2 = createGoal("book", user);
      Ddudu ddudu1 = createTodo(name, goal1, user);
      Ddudu ddudu2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();
      LocalDate mondayDate = date.with(DayOfWeek.MONDAY);
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      int dayIndex = dayOfWeek.getValue() - 1;

      // when
      List<TodoCompletionResponse> responses = todoService.findWeeklyCompletions(
          user.getId(), user.getId(), mondayDate);

      // then
      assertThat(responses).hasSize(7);
      assertThat(responses.get(dayIndex)).extracting("date", "totalCount", "uncompletedCount")
          .containsExactly(date, 2, 2);
    }

    @Test
    void 팔로워의_주간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(goalName, user);
      Goal goal2 = createGoal("book", user);
      Ddudu ddudu1 = createTodo(name, goal1, user);
      Ddudu ddudu2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();
      LocalDate mondayDate = date.with(DayOfWeek.MONDAY);
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      int dayIndex = dayOfWeek.getValue() - 1;

      FollowRequest request = new FollowRequest(user.getId());
      followingService.create(loginUser.getId(), request);

      // when
      List<TodoCompletionResponse> responses = todoService.findWeeklyCompletions(
          loginUser.getId(), user.getId(), mondayDate);

      // then
      assertThat(responses).hasSize(7);
      assertThat(responses.get(dayIndex)).extracting("date", "totalCount", "uncompletedCount")
          .containsExactly(date, 0, 0);
    }

    @Test
    void 다른_사용자의_주간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(goalName, user);
      Goal goal2 = createGoal("book", user);
      Ddudu ddudu1 = createTodo(name, goal1, user);
      Ddudu ddudu2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();
      LocalDate mondayDate = date.with(DayOfWeek.MONDAY);
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      int dayIndex = dayOfWeek.getValue() - 1;

      // when
      List<TodoCompletionResponse> responses = todoService.findWeeklyCompletions(
          loginUser.getId(), user.getId(), mondayDate);

      // then
      assertThat(responses).hasSize(7);
      assertThat(responses.get(dayIndex)).extracting("date", "totalCount", "uncompletedCount")
          .containsExactly(date, 0, 0);
    }

    @Test
    void 로그인_아이디가_존재하지_않아_주간_할_일_달성률_조회를_실패한다() {
      // given
      Long invalidLoginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LocalDate date = LocalDate.now();

      // when
      ThrowingCallable findWeeklyCompletions = () -> todoService.findWeeklyCompletions(
          invalidLoginId, user.getId(), date);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(findWeeklyCompletions)
          .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 사용자_아이디가_존재하지_않아_주간_할_일_달성률_조회를_실패한다() {
      // given
      Long invalidUserId = faker.random()
          .nextLong(Long.MAX_VALUE);
      LocalDate date = LocalDate.now();

      // when
      ThrowingCallable findWeeklyCompletions = () -> todoService.findWeeklyCompletions(
          loginUser.getId(), invalidUserId, date);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(findWeeklyCompletions)
          .withMessage(DduduErrorCode.USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 자신의_월간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(goalName, user);
      Goal goal2 = createGoal("book", user);
      Ddudu ddudu1 = createTodo(name, goal1, user);
      Ddudu ddudu2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();
      YearMonth yearMonth = YearMonth.now();
      int daysInMonth = yearMonth.lengthOfMonth();
      int dayOfMonthIndex = date.getDayOfMonth() - 1;

      // when
      List<TodoCompletionResponse> responses = todoService.findMonthlyCompletions(
          user.getId(), user.getId(), yearMonth);

      // then
      assertThat(responses).hasSize(daysInMonth);
      assertThat(responses.get(dayOfMonthIndex)).extracting(
              "date", "totalCount", "uncompletedCount")
          .containsExactly(date, 2, 2);
    }

    @Test
    void 팔로워의_월간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(goalName, user);
      Goal goal2 = createGoal("book", user);
      Ddudu ddudu1 = createTodo(name, goal1, user);
      Ddudu ddudu2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();
      YearMonth yearMonth = YearMonth.now();
      int daysInMonth = yearMonth.lengthOfMonth();
      int dayOfMonthIndex = date.getDayOfMonth() - 1;

      FollowRequest request = new FollowRequest(user.getId());
      followingService.create(loginUser.getId(), request);

      // when
      List<TodoCompletionResponse> responses = todoService.findMonthlyCompletions(
          loginUser.getId(), user.getId(), yearMonth);

      // then
      assertThat(responses).hasSize(daysInMonth);
      assertThat(responses.get(dayOfMonthIndex)).extracting(
              "date", "totalCount", "uncompletedCount")
          .containsExactly(date, 0, 0);
    }

    @Test
    void 다른_사용자의_월간_할_일_달성률_조회를_성공한다() {
      // given
      Goal goal1 = createGoal(goalName, user);
      Goal goal2 = createGoal("book", user);
      Ddudu ddudu1 = createTodo(name, goal1, user);
      Ddudu ddudu2 = createTodo("JPA N+1 문제 해결", goal1, user);

      LocalDate date = LocalDate.now();
      YearMonth yearMonth = YearMonth.now();
      int daysInMonth = yearMonth.lengthOfMonth();
      int dayOfMonthIndex = date.getDayOfMonth() - 1;

      // when
      List<TodoCompletionResponse> responses = todoService.findMonthlyCompletions(
          loginUser.getId(), user.getId(), yearMonth);

      // then
      assertThat(responses).hasSize(daysInMonth);
      assertThat(responses.get(dayOfMonthIndex)).extracting(
              "date", "totalCount", "uncompletedCount")
          .containsExactly(date, 0, 0);
    }

    @Test
    void 로그인_아이디가_존재하지_않아_월간_할_일_달성률_조회를_실패한다() {
      // given
      Long invalidLoginId = faker.random()
          .nextLong(Long.MAX_VALUE);
      YearMonth yearMonth = YearMonth.now();

      // when
      ThrowingCallable findMonthlyCompletions = () -> todoService.findMonthlyCompletions(
          invalidLoginId, user.getId(), yearMonth);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(findMonthlyCompletions)
          .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getMessage());
    }

    @Test
    void 사용자_아이디가_존재하지_않아_월간_할_일_달성률_조회를_실패한다() {
      // given
      Long invalidUserId = faker.random()
          .nextLong(Long.MAX_VALUE);
      YearMonth yearMonth = YearMonth.now();

      // when
      ThrowingCallable findMonthlyCompletions = () -> todoService.findMonthlyCompletions(
          loginUser.getId(), invalidUserId, yearMonth);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(findMonthlyCompletions)
          .withMessage(DduduErrorCode.USER_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 할_일_삭제_테스트 {

    @Test
    void 할_일을_삭제를_성공한다() {
      // given
      Goal goal = createGoal(goalName, user);
      Ddudu ddudu = createTodo(name, goal, user);

      Optional<Ddudu> found = oldTodoRepository.findById(ddudu.getId());
      assertThat(found).isNotEmpty();

      // when
      todoService.delete(user.getId(), ddudu.getId());
      flushAndClearPersistence();

      // then
      Optional<Ddudu> foundAfterDeleted = oldTodoRepository.findById(ddudu.getId());
      assertThat(foundAfterDeleted).isEmpty();
    }

    @Test
    void 로그인_사용자_아이디와_삭제할_할_일_사용자_아이디가_다르면_삭제를_실패한다() {
      // given
      Long userId = faker.random()
          .nextLong(Long.MAX_VALUE);
      Goal goal = createGoal(goalName, user);
      Ddudu ddudu = createTodo(name, goal, user);

      Optional<Ddudu> found = oldTodoRepository.findById(ddudu.getId());
      assertThat(found).isNotEmpty();

      // when
      ThrowingCallable delete = () -> todoService.delete(userId, ddudu.getId());

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(delete)
          .withMessage(DduduErrorCode.INVALID_AUTHORITY.getMessage());
    }

  }

  private Goal createGoal(String name, User user) {
    Goal goal = Goal.builder()
        .name(name)
        .user(user)
        .build();

    return oldGoalRepository.save(goal);
  }

  private Ddudu createTodo(String name, Goal goal, User user) {
    Ddudu ddudu = Ddudu.builder()
        .name(name)
        .goal(goal)
        .user(user)
        .build();

    return oldTodoRepository.save(ddudu);
  }

  private User createUser() {
    String email = faker.internet()
        .emailAddress();
    String password = faker.internet()
        .password(8, 40, true, true, true);
    String nickname = faker.oscarMovie()
        .character();

    User user = User.builder()
        .build();

    return userRepository.save(user);
  }

  private Like createLike(User user, Ddudu ddudu) {
    Like like = Like.builder()
        .user(user)
        .ddudu(ddudu)
        .build();
    return likeRepository.save(like);
  }

  private void flushAndClearPersistence() {
    entityManager.flush();
    entityManager.clear();
  }

}