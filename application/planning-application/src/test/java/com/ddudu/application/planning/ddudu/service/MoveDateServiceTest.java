package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.request.MoveDateRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.TodoLoaderPort;
import com.ddudu.application.common.port.ddudu.out.SaveTodoPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
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
class MoveDateServiceTest {

  @Autowired
  MoveDateService moveDateService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  TodoLoaderPort dduduLoaderPort;

  User user;
  Goal goal;
  Todo ddudu;
  LocalDate tomorrow;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
    tomorrow = LocalDate.now()
        .plusDays(1);
  }

  @Test
  void 투두를_미루기_한다() {
    // given
    final LocalDate previousScheduledOn = ddudu.getScheduledOn();
    MoveDateRequest request = new MoveDateRequest(tomorrow, true);

    // when
    moveDateService.moveDate(user.getId(), ddudu.getId(), request);

    // then
    Todo actual = dduduLoaderPort.getTodoOrElseThrow(ddudu.getId(), "not found");

    assertThat(actual.getScheduledOn()).isEqualTo(tomorrow);
    assertThat(actual.isPostponed()).isTrue();
    assertThat(actual.getPostponedAt()).isEqualTo(previousScheduledOn.atStartOfDay());
  }

  @Test
  void 투두를_오늘_다시_하기_한다() {
    // given
    LocalDate yesterday = LocalDate.now()
        .minusDays(1);
    Todo pastTodo = saveTodoPort.save(TodoFixture.createRandomTodoWithSchedule(
        user.getId(),
        goal.getId(),
        yesterday
    ));
    MoveDateRequest request = new MoveDateRequest(LocalDate.now(), false);

    // when
    moveDateService.moveDate(user.getId(), pastTodo.getId(), request);

    // then
    Todo actual = dduduLoaderPort.getTodoOrElseThrow(pastTodo.getId(), "not found");

    assertThat(actual.getScheduledOn()).isEqualTo(LocalDate.now());
  }

  @Test
  void 완료한_지난_투두의_날짜를_바꾼다() {
    // given
    LocalDate twoDaysAgo = LocalDate.now()
        .minusDays(2);
    Todo pastTodo = saveTodoPort.save(TodoFixture.createRandomTodoWithSchedule(
        user.getId(),
        goal.getId(),
        twoDaysAgo
    ));
    LocalDate yesterday = LocalDate.now()
        .minusDays(1);
    pastTodo = saveTodoPort.save(TodoFixture.createRandomTodoWithStatusAndSchedule(
        goal,
        TodoStatus.COMPLETE,
        twoDaysAgo
    ));
    MoveDateRequest request = new MoveDateRequest(yesterday, false);

    // when
    moveDateService.moveDate(user.getId(), pastTodo.getId(), request);

    // then
    Todo actual = dduduLoaderPort.getTodoOrElseThrow(pastTodo.getId(), "not found");

    assertThat(actual.getScheduledOn()).isEqualTo(yesterday);
  }

  @Test
  void 완료된_투두에_미루기_요청을_하면_실패한다() {
    // given
    Todo completedTodo = saveTodoPort.save(TodoFixture.createRandomTodoWithStatus(
        goal,
        TodoStatus.COMPLETE
    ));
    MoveDateRequest request = new MoveDateRequest(tomorrow, true);

    // when
    ThrowingCallable moveDate = () -> moveDateService.moveDate(user.getId(), completedTodo.getId(),
        request);

    // then
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(moveDate)
        .withMessage(TodoErrorCode.UNABLE_TO_POSTPONE_COMPLETED_DDUDU.getCodeName());
  }

  @Test
  void 투두가_존재하지_않으면_날짜_변경을_실패한다() {
    // given
    long invalidId = TodoFixture.getRandomId();
    MoveDateRequest request = new MoveDateRequest(tomorrow, true);

    // when
    ThrowingCallable moveDate = () -> moveDateService.moveDate(user.getId(), invalidId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(moveDate)
        .withMessage(TodoErrorCode.ID_NOT_EXISTING.getCodeName());
  }

}
