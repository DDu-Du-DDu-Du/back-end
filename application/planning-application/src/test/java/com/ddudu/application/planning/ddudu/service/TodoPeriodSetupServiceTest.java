package com.ddudu.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.todo.request.PeriodSetupRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalTime;
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
class TodoPeriodSetupServiceTest {

  @Autowired
  PeriodSetupService todoPeriodSetupService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  TodoLoaderPort todoLoaderPort;

  User user;
  Todo todo;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    todo = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
  }

  @Test
  void 투두_시작_및_종료시간을_설정한다() {
    // given
    LocalTime now = LocalTime.now();
    PeriodSetupRequest request = new PeriodSetupRequest(now, LocalTime.MAX);

    // when
    todoPeriodSetupService.setUpPeriod(user.getId(), todo.getId(), request);

    // then
    Todo actual = todoLoaderPort.getTodoOrElseThrow(todo.getId(), "not found");

    assertThat(actual.getBeginAt()).isEqualTo(now);
    assertThat(actual.getEndAt()).isEqualTo(LocalTime.MAX);
  }

  @Test
  void 투두가_없으면_시간_설정을_실패한다() {
    // given
    LocalTime now = LocalTime.now();
    PeriodSetupRequest request = new PeriodSetupRequest(now, LocalTime.MAX);
    long invalidId = TodoFixture.getRandomId();

    // when
    ThrowingCallable setUpPeriod = () -> todoPeriodSetupService.setUpPeriod(
        user.getId(),
        invalidId,
        request
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(setUpPeriod)
        .withMessage(TodoErrorCode.ID_NOT_EXISTING.getCodeName());
  }

}
