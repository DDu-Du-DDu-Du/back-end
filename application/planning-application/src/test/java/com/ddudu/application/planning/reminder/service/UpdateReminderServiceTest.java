package com.ddudu.application.planning.reminder.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.dto.reminder.request.UpdateReminderRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.common.exception.ReminderErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.ReminderFixture;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@RecordApplicationEvents
@DisplayNameGeneration(ReplaceUnderscores.class)
class UpdateReminderServiceTest {

  @Autowired
  UpdateReminderService updateReminderService;

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

  @SpyBean
  TodoLoaderPort todoLoaderPort;

  User user;
  Todo todo;
  Reminder reminder;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    Todo temp = TodoFixture.createRandomTodoWithGoalAndTime(goal, LocalTime.of(10, 0), null);
    todo = saveTodoPort.save(temp.moveDate(LocalDate.now().plusDays(1)));
    reminder = reminderCommandPort.save(
        ReminderFixture.createReminderWithUserIdAndTodoId(user.getId(), todo.getId())
    );
  }

  @Test
  void 미리알림_갱신에_성공한다() {
    // given
    LocalDateTime newRemindsAt = todo.getScheduledOn().atTime(todo.getBeginAt()).minusMinutes(20);
    UpdateReminderRequest request = new UpdateReminderRequest(newRemindsAt);

    // when
    updateReminderService.update(user.getId(), reminder.getId(), request);

    // then
    Reminder updated = reminderLoaderPort.getOptionalReminder(reminder.getId()).orElseThrow();
    assertThat(updated.getRemindsAt()).isEqualTo(newRemindsAt);
  }

  @Test
  void 미리알림_갱신_후_이벤트를_발행한다(ApplicationEvents events) {
    // given
    LocalDateTime newRemindsAt = todo.getScheduledOn().atTime(todo.getBeginAt()).minusMinutes(25);
    UpdateReminderRequest request = new UpdateReminderRequest(newRemindsAt);

    // when
    updateReminderService.update(user.getId(), reminder.getId(), request);

    // then
    assertThat(events.stream(InterimSetReminderEvent.class)).hasSize(1);
  }

  @Test
  void 로그인_사용자가_없으면_실패한다() {
    // given
    Long invalidLoginId = TodoFixture.getRandomId();
    UpdateReminderRequest request = new UpdateReminderRequest(
        todo.getScheduledOn().atTime(todo.getBeginAt()).minusMinutes(20)
    );

    // when
    ThrowingCallable update = () -> updateReminderService.update(
        invalidLoginId,
        reminder.getId(),
        request
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(update)
        .withMessage(ReminderErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 미리알림이_없으면_실패한다() {
    // given
    Long invalidReminderId = TodoFixture.getRandomId();
    UpdateReminderRequest request = new UpdateReminderRequest(
        todo.getScheduledOn().atTime(todo.getBeginAt()).minusMinutes(20)
    );

    // when
    ThrowingCallable update = () -> updateReminderService.update(
        user.getId(),
        invalidReminderId,
        request
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(update)
        .withMessage(ReminderErrorCode.REMINDER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 투두가_없으면_실패한다() {
    // given
    doThrow(
        new MissingResourceException(
            ReminderErrorCode.TODO_NOT_EXISTING.getCodeName(),
            Todo.class.getName(),
            String.valueOf(todo.getId())
        )
    ).when(todoLoaderPort).getTodoOrElseThrow(eq(todo.getId()), anyString());
    UpdateReminderRequest request = new UpdateReminderRequest(LocalDateTime.now().plusDays(1));

    // when
    ThrowingCallable update = () -> updateReminderService.update(
        user.getId(),
        reminder.getId(),
        request
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(update)
        .withMessage(ReminderErrorCode.TODO_NOT_EXISTING.getCodeName());
  }

}
