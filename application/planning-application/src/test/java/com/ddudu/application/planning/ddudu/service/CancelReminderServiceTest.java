package com.ddudu.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.NotificationEventFixture;
import com.ddudu.fixture.TodoFixture;
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
class CancelReminderServiceTest {

  @Autowired
  CancelReminderService cancelReminderService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  TodoLoaderPort todoLoaderPort;

  @Autowired
  NotificationEventLoaderPort notificationEventLoaderPort;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  User user;
  Goal goal;
  Todo todo;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    LocalTime beginAt = LocalTime.MAX;
    todo = TodoFixture.createRandomTodoWithGoalAndTime(
        goal,
        beginAt,
        null
    );
    todo = todo.moveDate(LocalDate.now()
        .plusDays(1));
    todo = saveTodoPort.save(todo.setReminder(0, 0, 10));
  }

  @Test
  void 미리알림_취소를_성공한다() {
    // given
    NotificationEvent event = NotificationEventFixture.createValidTodoEventNowWithUserAndContext(
        user.getId(),
        todo.getId()
    );

    notificationEventCommandPort.save(event);

    // when
    cancelReminderService.cancel(user.getId(), todo.getId());

    // then
    Todo actual = todoLoaderPort.getTodoOrElseThrow(todo.getId(), "not found");

    assertThat(actual.getRemindAt()).isNull();
  }

  @Test
  void 존재하지_않는_사용자는_미리알림_취소에_실패한다() {
    // given
    long invalidId = UserFixture.getRandomId();

    // when
    ThrowingCallable cancel = () -> cancelReminderService.cancel(invalidId, todo.getId());

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(cancel)
        .withMessage(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 존재하지_않는_투두는_미리알림_취소에_실패한다() {
    // given
    long invalidId = TodoFixture.getRandomId();

    // when
    ThrowingCallable cancel = () -> cancelReminderService.cancel(user.getId(), invalidId);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(cancel)
        .withMessage(TodoErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 다른_사용자는_미리알림_취소에_실패한다() {
    // given
    User another = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable cancel = () -> cancelReminderService.cancel(another.getId(), todo.getId());

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(cancel)
        .withMessage(TodoErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
