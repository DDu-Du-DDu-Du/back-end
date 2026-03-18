package com.ddudu.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.todo.request.RepeatAnotherDayRequest;
import com.ddudu.application.common.dto.todo.response.RepeatAnotherDayResponse;
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
class RepeatServiceTest {

  @Autowired
  RepeatService repeatService;

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

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    todo = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
  }

  @Test
  void 투두_다른_날_반복하기를_성공한다() {
    // given
    LocalDate tomorrow = LocalDate.now()
        .plusDays(1);
    RepeatAnotherDayRequest request = new RepeatAnotherDayRequest(tomorrow);

    // when
    RepeatAnotherDayResponse response = repeatService.repeatOnAnotherDay(
        user.getId(),
        todo.getId(),
        request
    );

    // then
    Todo actual = todoLoaderPort.getTodoOrElseThrow(response.id(), "not found");

    assertThat(actual.getScheduledOn()).isEqualTo(tomorrow);
    assertThat(actual).isNotEqualTo(todo);
  }

  @Test
  void 투두가_없으면_다른_날_반복하기를_실패한다() {
    // given
    long invalidId = TodoFixture.getRandomId();
    LocalDate tomorrow = LocalDate.now()
        .plusDays(1);
    RepeatAnotherDayRequest request = new RepeatAnotherDayRequest(tomorrow);

    // when
    ThrowingCallable repeat = () -> repeatService.repeatOnAnotherDay(
        user.getId(),
        invalidId,
        request
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(repeat)
        .withMessage(TodoErrorCode.ID_NOT_EXISTING.getCodeName());
  }

}
