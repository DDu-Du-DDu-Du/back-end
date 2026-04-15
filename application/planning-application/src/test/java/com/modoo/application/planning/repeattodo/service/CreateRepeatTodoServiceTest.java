package com.modoo.application.planning.repeattodo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.dto.repeattodo.request.CreateRepeatTodoRequest;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.common.exception.GoalErrorCode;
import com.modoo.common.exception.RepeatTodoErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.modoo.domain.planning.repeattodo.aggregate.enums.RepeatType;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.RepeatTodoFixture;
import com.modoo.fixture.UserFixture;
import java.time.LocalDate;
import java.time.YearMonth;
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
class CreateRepeatTodoServiceTest {

  @Autowired
  CreateRepeatTodoService createRepeatTodoService;

  @Autowired
  RepeatTodoLoaderPort repeatTodoLoaderPort;

  @Autowired
  TodoLoaderPort todoLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  User user;
  Goal goal;
  String name;
  LocalDate startDate;
  LocalDate endDate;
  RepeatType repeatType;
  List<String> repeatDaysOfWeek;
  List<Integer> repeatDaysOfMonth;
  Boolean lastDayOfMonth;
  CreateRepeatTodoRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    name = RepeatTodoFixture.getRandomSentenceWithMax(50);
    startDate = LocalDate.now();
    endDate = LocalDate.now()
        .plusMonths(1);
    repeatType = RepeatTodoFixture.getRandomRepeatType();
    repeatDaysOfWeek = RepeatTodoFixture.getRandomRepeatDaysOfWeek();
    repeatDaysOfMonth = RepeatTodoFixture.getRandomRepeatDaysOfMonth(
        startDate.getDayOfMonth(),
        YearMonth.now()
            .atEndOfMonth()
            .getDayOfMonth()
    );
    lastDayOfMonth = true;
    request = new CreateRepeatTodoRequest(
        name,
        goal.getId(),
        repeatType.name(),
        repeatDaysOfWeek,
        repeatDaysOfMonth,
        lastDayOfMonth,
        startDate,
        endDate,
        null,
        null
    );
  }

  @Test
  void 반복_투두_생성에_성공한다() {
    // when
    Long repeatTodoId = createRepeatTodoService.create(user.getId(), request);

    // then
    RepeatTodo repeatTodo = repeatTodoLoaderPort.getOptionalRepeatTodo(repeatTodoId)
        .get();

    assertThat(repeatTodo)
        .hasFieldOrPropertyWithValue("name", name)
        .hasFieldOrPropertyWithValue("goalId", goal.getId())
        .hasFieldOrPropertyWithValue("repeatType", repeatType)
        .hasFieldOrPropertyWithValue("startDate", startDate)
        .hasFieldOrPropertyWithValue("endDate", endDate);
  }

  @Test
  void 반복_투두_생성_시_투두도_함께_생성된다() {
    // given

    // when
    Long repeatTodoId = createRepeatTodoService.create(user.getId(), request);

    // then
    RepeatTodo repeatTodo = repeatTodoLoaderPort.getOptionalRepeatTodo(repeatTodoId)
        .get();
    List<Todo> todos = todoLoaderPort.getRepeatedTodos(repeatTodo);

    Assertions.assertThat(todos)
        .isNotEmpty();
  }

  @Test
  void 목표_아이디가_유효하지_않으면_예외가_발생한다() {
    // given
    Long invalidGoalId = GoalFixture.getRandomId();
    CreateRepeatTodoRequest request = new CreateRepeatTodoRequest(
        name,
        invalidGoalId,
        repeatType.name(),
        repeatDaysOfWeek,
        repeatDaysOfMonth,
        lastDayOfMonth,
        startDate,
        endDate,
        null,
        null
    );

    // when
    ThrowingCallable create = () -> createRepeatTodoService.create(user.getId(), request);

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(create)
        .withMessage(RepeatTodoErrorCode.INVALID_GOAL.getCodeName());
  }

  @Test
  void 본인의_목표가_아닌_경우_예외가_발생한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable create = () -> createRepeatTodoService.create(anotherUser.getId(), request);

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(create)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
