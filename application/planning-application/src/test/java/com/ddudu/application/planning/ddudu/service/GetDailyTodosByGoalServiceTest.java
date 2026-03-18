package com.ddudu.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.todo.GoalGroupedTodos;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.util.List;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class GetDailyTodosByGoalServiceTest {

  @Autowired
  GetDailyTodosByGoalService getDailyTodosByGoalService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  User user;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
  }

  @Test
  void 주어진_날짜에_자신의_목표별_투두_리스트_조회를_성공한다() {
    // given
    Goal goal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user.getId(), PrivacyType.PRIVATE));
    Todo todo = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));

    LocalDate date = LocalDate.now();

    // when
    List<GoalGroupedTodos> responses = getDailyTodosByGoalService.get(
        user.getId(),
        user.getId(),
        date
    );

    // then
    Assertions.assertThat(responses)
        .hasSize(1);

    GoalGroupedTodos firstElement = responses.get(0);
    assertThat(firstElement.goal()).extracting("id")
        .isEqualTo(goal.getId());
    assertThat(firstElement.todos()).extracting("id")
        .containsExactly(todo.getId());
    assertThat(firstElement.todos().get(0)
        .postponedAt()).isNull();
  }

  @Test
  void 미룬_투두_조회시_미루기_일시를_포함한다() {
    // given
    LocalDate date = LocalDate.now();
    Goal goal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user.getId(), PrivacyType.PRIVATE));
    saveTodoPort.save(TodoFixture.createTodoWithScheduleAndPostponedFlag(goal, true, date));

    // when
    List<GoalGroupedTodos> responses = getDailyTodosByGoalService.get(
        user.getId(),
        user.getId(),
        date
    );

    // then
    assertThat(responses.get(0)
        .todos().get(0)
        .postponedAt()).isNotNull();
  }

  @Test
  void 다른_사용자의_목표별_투두을_조회할_경우_전체공개_목표의_투두만_조회한다() {
    // given
    Goal publicGoal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user.getId(), PrivacyType.PUBLIC));
    saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(publicGoal));

    Goal privateGoal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user.getId(), PrivacyType.PRIVATE));
    saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(privateGoal));

    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    LocalDate date = LocalDate.now();

    // when
    List<GoalGroupedTodos> responses = getDailyTodosByGoalService.get(
        anotherUser.getId(),
        user.getId(),
        date
    );

    // then
    Assertions.assertThat(responses)
        .hasSize(1);
    assertThat(responses.get(0)
        .goal()).extracting("id")
        .isEqualTo(publicGoal.getId());
  }


  @Test
  void 로그인_아이디가_존재하지_않아_목표별_투두_조회를_실패한다() {
    // given
    Long invalidLoginId = UserFixture.getRandomId();
    LocalDate date = LocalDate.now();

    // when
    ThrowingCallable findAllByDate = () -> getDailyTodosByGoalService.get(
        invalidLoginId,
        user.getId(),
        date
    );

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(findAllByDate)
        .withMessage(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 사용자_아이디가_존재하지_않아_목표별_투두_조회를_실패한다() {
    // given
    Long loginUserId = user.getId();
    Long invalidUserId = UserFixture.getRandomId();
    LocalDate date = LocalDate.now();

    // when
    ThrowingCallable findAllByDate = () -> getDailyTodosByGoalService.get(
        loginUserId,
        invalidUserId,
        date
    );

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(findAllByDate)
        .withMessage(TodoErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
