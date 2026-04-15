package com.modoo.application.planning.reminder.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.reminder.out.ReminderCommandPort;
import com.modoo.application.common.port.reminder.out.ReminderLoaderPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.common.exception.ReminderErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.reminder.aggregate.Reminder;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.ReminderFixture;
import com.modoo.fixture.TodoFixture;
import com.modoo.fixture.UserFixture;
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
class CancelReminderByIdServiceTest {

  @Autowired
  CancelReminderByIdService cancelReminderByIdService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  ReminderCommandPort reminderCommandPort;

  @Autowired
  ReminderLoaderPort reminderLoaderPort;

  User user;
  Todo todo;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    todo = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
  }

  @Test
  void 미리알림_취소에_성공한다() {
    // given
    Reminder reminder = reminderCommandPort.save(
        ReminderFixture.createReminderWithUserIdAndTodoId(user.getId(), todo.getId())
    );

    // when
    cancelReminderByIdService.cancel(user.getId(), reminder.getId());

    // then
    assertThat(reminderLoaderPort.getOptionalReminder(reminder.getId())).isEmpty();
  }

  @Test
  void 존재하지_않는_미리알림_아이디면_성공한다() {
    // given
    Long unknownReminderId = TodoFixture.getRandomId();

    // when
    cancelReminderByIdService.cancel(user.getId(), unknownReminderId);

    // then

  }

  @Test
  void 로그인_사용자가_없으면_실패한다() {
    // given
    Long invalidLoginId = TodoFixture.getRandomId();
    Reminder reminder = reminderCommandPort.save(
        ReminderFixture.createReminderWithUserIdAndTodoId(user.getId(), todo.getId())
    );

    // when
    ThrowingCallable cancel = () -> cancelReminderByIdService.cancel(
        invalidLoginId,
        reminder.getId()
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(cancel)
        .withMessage(ReminderErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }


}
