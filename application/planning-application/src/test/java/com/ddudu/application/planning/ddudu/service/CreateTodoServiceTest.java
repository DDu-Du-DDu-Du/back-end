package com.ddudu.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.todo.request.CreateTodoRequest;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
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
class CreateTodoServiceTest {

  @Autowired
  CreateTodoService createTodoService;

  @Autowired
  TodoLoaderPort todoLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  User user;
  Goal goal;
  String name;
  LocalDate scheduledOn;
  String memo;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    name = TodoFixture.getRandomSentenceWithMax(50);
    scheduledOn = LocalDate.now();
    memo = TodoFixture.createValidMemo();
  }


  @Test
  void 할_일_생성에_성공한다() {
    // given
    CreateTodoRequest request = new CreateTodoRequest(
        goal.getId(),
        name,
        memo,
        scheduledOn,
        null,
        null,
        null
    );

    // when
    BasicTodoResponse response = createTodoService.create(user.getId(), request);

    // then
    Todo actual = todoLoaderPort.getTodoOrElseThrow(
        response.id(),
        "할 일이 생성되지 않았습니다."
    );
    assertThat(actual).extracting(
            "name",
            "memo",
            "scheduledOn",
            "goalId",
            "userId",
            "status",
            "postponedAt"
        )
        .containsExactly(
            name,
            memo,
            scheduledOn,
            goal.getId(),
            user.getId(),
            TodoStatus.UNCOMPLETED,
            null);
  }

  @Test
  void 날짜를_설정하지_않은_경우_기본값이_적용된다() {
    // given
    CreateTodoRequest request = new CreateTodoRequest(
        goal.getId(),
        name,
        memo,
        null,
        null,
        null,
        null
    );

    // when
    BasicTodoResponse response = createTodoService.create(user.getId(), request);

    // then
    Todo actual = todoLoaderPort.getTodoOrElseThrow(
        response.id(),
        "할 일이 생성되지 않았습니다."
    );
    assertThat(actual.getScheduledOn()).isEqualTo(LocalDate.now());
  }

  @Test
  void 사용자_아이디가_유효하지_않으면_예외가_발생한다() {
    // give
    Long invalidUserId = UserFixture.getRandomId();
    CreateTodoRequest request = new CreateTodoRequest(
        goal.getId(),
        name,
        memo,
        scheduledOn,
        null,
        null,
        null
    );

    // when
    ThrowingCallable create = () -> createTodoService.create(invalidUserId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(create)
        .withMessage(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 목표_아이디가_유효하지_않으면_예외가_발생한다() {
    // given
    Long invalidGoalId = GoalFixture.getRandomId();
    CreateTodoRequest request = new CreateTodoRequest(
        invalidGoalId,
        name,
        memo,
        scheduledOn,
        null,
        null,
        null
    );

    // when
    ThrowingCallable create = () -> createTodoService.create(user.getId(), request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(create)
        .withMessage(TodoErrorCode.GOAL_NOT_EXISTING.getCodeName());
  }

  @Test
  void 본인의_목표가_아닌_경우_예외가_발생한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goalOfAnotherUser = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUser(
            anotherUser.getId()
        )
    );
    CreateTodoRequest request = new CreateTodoRequest(
        goalOfAnotherUser.getId(),
        name,
        memo,
        scheduledOn,
        null,
        null,
        null
    );

    // when
    ThrowingCallable create = () -> createTodoService.create(user.getId(), request);

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(create)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
