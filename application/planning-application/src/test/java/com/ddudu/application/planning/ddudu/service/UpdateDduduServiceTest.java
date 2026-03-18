package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.request.UpdateTodoRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicTodoResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveTodoPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
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
class UpdateTodoServiceTest {

  @Autowired
  UpdateTodoService updateTodoService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  User user;
  Goal goal;
  Todo ddudu;
  UpdateTodoRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
    request = new UpdateTodoRequest(
        goal.getId(),
        TodoFixture.getRandomSentenceWithMax(50),
        TodoFixture.createValidMemo(),
        LocalDate.now().plusDays(1),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        null,
        null,
        null
    );
  }

  @Test
  void 투두를_수정한다() {
    // given

    // when
    BasicTodoResponse actual = updateTodoService.update(
        user.getId(),
        ddudu.getId(),
        request
    );

    // then
    assertThat(actual.id()).isEqualTo(ddudu.getId());
    assertThat(actual.name()).isEqualTo(request.name());
  }

  @Test
  void 로그인_사용자가_없으면_수정에_실패한다() {
    // given
    Long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable update = () -> updateTodoService.update(
        invalidUserId,
        ddudu.getId(),
        request
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 존재하지_않는_투두면_수정에_실패한다() {
    // given
    Long invalidTodoId = TodoFixture.getRandomId();

    // when
    ThrowingCallable update = () -> updateTodoService.update(
        user.getId(),
        invalidTodoId,
        request
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(TodoErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 작성자가_아니면_수정에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable update = () -> updateTodoService.update(
        anotherUser.getId(),
        ddudu.getId(),
        request
    );

    // then
    Assertions.assertThatThrownBy(update)
        .isInstanceOf(SecurityException.class)
        .hasMessage(TodoErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
