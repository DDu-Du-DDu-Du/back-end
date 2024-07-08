package com.ddudu.old.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.old.goal.domain.OldGoalRepository;
import com.ddudu.old.like.domain.Like;
import com.ddudu.old.like.domain.LikeRepository;
import com.ddudu.old.todo.domain.OldTodoRepository;
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
      List<DduduCompletionResponse> responses = todoService.findWeeklyCompletions(
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
      List<DduduCompletionResponse> responses = todoService.findWeeklyCompletions(
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
      List<DduduCompletionResponse> responses = todoService.findWeeklyCompletions(
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
      List<DduduCompletionResponse> responses = todoService.findMonthlyCompletions(
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
      List<DduduCompletionResponse> responses = todoService.findMonthlyCompletions(
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
      List<DduduCompletionResponse> responses = todoService.findMonthlyCompletions(
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
