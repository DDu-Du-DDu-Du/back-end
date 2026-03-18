package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.request.ChangeNameRequest;
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
class ChangeNameServiceTest {

  @Autowired
  ChangeNameService changeNameService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  User user;
  Goal goal;
  Todo ddudu;
  ChangeNameRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
    request = new ChangeNameRequest(TodoFixture.getRandomSentenceWithMax(50));
  }

  @Test
  void 투두의_이름을_변경한다() {
    // when
    BasicTodoResponse actual = changeNameService.change(user.getId(), ddudu.getId(), request);

    // then
    assertThat(actual.name()).isEqualTo(request.name());
  }

  @Test
  void 변경할_이름이_50자가_넘으면_변경에_실패한다() {
    // given
    request = new ChangeNameRequest(TodoFixture.getRandomSentence(51, 100));

    // when
    ThrowingCallable changeName = () -> changeNameService.change(
        user.getId(),
        ddudu.getId(),
        request
    );

    // then
    Assertions.assertThatIllegalArgumentException()
        .isThrownBy(changeName)
        .withMessage(TodoErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
  }

  @Test
  void 존재하지_않는_투두인_경우_변경에_실패한다() {
    // given
    Long invalidId = TodoFixture.getRandomId();

    // when
    ThrowingCallable changeName = () -> changeNameService.change(user.getId(), invalidId, request);

    // then
    Assertions.assertThatThrownBy(changeName)
        .isInstanceOf(MissingResourceException.class)
        .hasMessage(TodoErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 투두를_생성한_사용자가_아닌_경우_변경에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable changeName = () -> changeNameService.change(
        anotherUser.getId(),
        ddudu.getId(),
        request
    );

    // then
    Assertions.assertThatThrownBy(changeName)
        .isInstanceOf(SecurityException.class)
        .hasMessage(TodoErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
