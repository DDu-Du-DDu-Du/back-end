package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.TodoLoaderPort;
import com.ddudu.application.common.port.ddudu.out.SaveTodoPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
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
class DeleteTodoServiceTest {

  @Autowired
  DeleteTodoService deleteTodoService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  TodoLoaderPort dduduLoaderPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  NotificationEventLoaderPort notificationEventLoaderPort;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  User user;
  Todo ddudu;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
  }

  @Test
  void 투두를_삭제_할_수_있다() {
    // when
    deleteTodoService.delete(user.getId(), ddudu.getId());

    // then
    assertThat(dduduLoaderPort.getOptionalTodo(ddudu.getId())).isEmpty();
  }

  @Test
  void 투두가_존재하지_않는_경우_예외가_발생하지_않는다() {
    // given
    Long invalidId = TodoFixture.getRandomId();

    // when
    ThrowingCallable delete = () -> deleteTodoService.delete(user.getId(), invalidId);

    // then
    assertThatNoException().isThrownBy(delete);
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_삭제에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable delete = () -> deleteTodoService.delete(anotherUser.getId(), ddudu.getId());

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(delete)
        .withMessage(TodoErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
