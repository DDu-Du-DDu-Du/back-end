package com.modoo.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

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
import java.util.MissingResourceException;
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
class SwitchStatusServiceTest {

  @Autowired
  SwitchStatusService switchStatusService;

  @Autowired
  TodoLoaderPort todoLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

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
  void 할_일_상태_업데이트를_성공한다() {
    // given
    TodoStatus beforeUpdated = todo.getStatus();

    // when
    switchStatusService.switchStatus(user.getId(), todo.getId());

    // then
    Todo actual = todoLoaderPort.getTodoOrElseThrow(todo.getId(), "할 일이 존재하지 않습니다.");
    assertThat(actual.getStatus()).isNotEqualTo(beforeUpdated);
  }

  @Test
  void 아이디가_존재하지_않아_할_일_상태_업데이트를_실패한다() {
    // given
    Long invalidTodoId = TodoFixture.getRandomId();

    // when
    ThrowingCallable switchStatus = () -> switchStatusService.switchStatus(
        user.getId(),
        invalidTodoId
    );

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(switchStatus)
        .withMessage(TodoErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자와_할_일_사용자가_다르면_상태_업데이트_실패한다() {
    // given
    Long anotherUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable updateStatus = () -> switchStatusService.switchStatus(
        anotherUserId,
        todo.getId()
    );

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(updateStatus)
        .withMessage(TodoErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
