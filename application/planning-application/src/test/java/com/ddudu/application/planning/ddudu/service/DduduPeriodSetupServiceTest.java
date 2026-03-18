package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.request.PeriodSetupRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.TodoLoaderPort;
import com.ddudu.application.common.port.ddudu.out.SaveTodoPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.GoalFixture;
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
  PeriodSetupService dduduPeriodSetupService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  TodoLoaderPort dduduLoaderPort;

  User user;
  Todo ddudu;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
  }

  @Test
  void 투두_시작_및_종료시간을_설정한다() {
    // given
    LocalTime now = LocalTime.now();
    PeriodSetupRequest request = new PeriodSetupRequest(now, LocalTime.MAX);

    // when
    dduduPeriodSetupService.setUpPeriod(user.getId(), ddudu.getId(), request);

    // then
    Todo actual = dduduLoaderPort.getTodoOrElseThrow(ddudu.getId(), "not found");

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
    ThrowingCallable setUpPeriod = () -> dduduPeriodSetupService.setUpPeriod(
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
