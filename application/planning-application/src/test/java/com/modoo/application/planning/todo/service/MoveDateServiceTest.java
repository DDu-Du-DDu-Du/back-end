package com.modoo.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.dto.todo.request.MoveDateRequest;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.planning.todo.aggregate.enums.TodoStatus;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.TodoFixture;
import com.modoo.fixture.UserFixture;
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
  TodoLoaderPort todoLoaderPort;

  User user;
  Goal goal;
  Todo todo;
  LocalDate tomorrow;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    todo = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
    tomorrow = LocalDate.now()
        .plusDays(1);
  }

  @Test
  void 투두를_미루기_한다() {
    // given
    final LocalDate previousScheduledOn = todo.getScheduledOn();
    MoveDateRequest request = new MoveDateRequest(tomorrow, true, null);

    // when
    moveDateService.moveDate(user.getId(), todo.getId(), request);

    // then
    Todo actual = todoLoaderPort.getTodoOrElseThrow(todo.getId(), "not found");

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
    MoveDateRequest request = new MoveDateRequest(LocalDate.now(), false, null);

    // when
    moveDateService.moveDate(user.getId(), pastTodo.getId(), request);

    // then
    Todo actual = todoLoaderPort.getTodoOrElseThrow(pastTodo.getId(), "not found");

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
    MoveDateRequest request = new MoveDateRequest(yesterday, false, null);

    // when
    moveDateService.moveDate(user.getId(), pastTodo.getId(), request);

    // then
    Todo actual = todoLoaderPort.getTodoOrElseThrow(pastTodo.getId(), "not found");

    assertThat(actual.getScheduledOn()).isEqualTo(yesterday);
  }

  @Test
  void 완료된_투두에_미루기_요청을_하면_실패한다() {
    // given
    Todo completedTodo = saveTodoPort.save(TodoFixture.createRandomTodoWithStatus(
        goal,
        TodoStatus.COMPLETE
    ));
    MoveDateRequest request = new MoveDateRequest(tomorrow, true, null);

    // when
    ThrowingCallable moveDate = () -> moveDateService.moveDate(
        user.getId(), completedTodo.getId(),
        request
    );

    // then
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(moveDate)
        .withMessage(TodoErrorCode.UNABLE_TO_POSTPONE_COMPLETED_TODO.getCodeName());
  }

  @Test
  void 투두가_존재하지_않으면_날짜_변경을_실패한다() {
    // given
    long invalidId = TodoFixture.getRandomId();
    MoveDateRequest request = new MoveDateRequest(tomorrow, true, null);

    // when
    ThrowingCallable moveDate = () -> moveDateService.moveDate(user.getId(), invalidId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(moveDate)
        .withMessage(TodoErrorCode.ID_NOT_EXISTING.getCodeName());
  }

}
