package com.ddudu.application.notification.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.notification.in.SendNotificationEventUseCase;
import com.ddudu.application.common.port.notification.out.NotificationDeviceTokenCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.common.exception.NotificationEventErrorCode;
import com.ddudu.common.exception.ReminderErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.NotificationDeviceTokenFixture;
import com.ddudu.fixture.NotificationEventFixture;
import com.ddudu.fixture.ReminderFixture;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDateTime;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RequiredArgsConstructor
@Transactional
class SendNotificationEventServiceTest {

  @Autowired
  SendNotificationEventUseCase sendNotificationEventUseCase;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  @Autowired
  NotificationEventLoaderPort notificationEventLoaderPort;

  @Autowired
  NotificationDeviceTokenCommandPort notificationDeviceTokenCommandPort;

  @Autowired
  ReminderCommandPort reminderCommandPort;

  User user;
  Goal goal;
  Todo ddudu;
  Reminder reminder;
  NotificationEvent notificationEvent;

  @BeforeEach
  void setUp() {
    LocalDateTime scheduledAt = LocalDateTime.now()
        .plusSeconds(2);
    LocalDateTime remindAt = LocalDateTime.now()
        .plusSeconds(1);
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveTodoPort.save(TodoFixture.createTodoWithReminder(
        user.getId(),
        goal.getId(),
        scheduledAt.toLocalDate(),
        scheduledAt.toLocalTime(),
        remindAt
    ));
    reminder = reminderCommandPort.save(
        ReminderFixture.createReminderWithUserIdAndTodoId(user.getId(), ddudu.getId())
    );
    notificationEvent = NotificationEventFixture.createValidTodoEventNowWithUserAndContext(
        user.getId(),
        reminder.getId()
    );
    notificationEvent = notificationEventCommandPort.save(notificationEvent);

    notificationDeviceTokenCommandPort.save(
        NotificationDeviceTokenFixture.createWithUser(user.getId())
    );
  }

  @Test
  void 알림_발송을_성공한다() {
    // given
    LocalDateTime expected = notificationEvent.getFiredAt();

    // when
    sendNotificationEventUseCase.send(notificationEvent.getId());

    // then
    NotificationEvent actual = notificationEventLoaderPort.getEventOrElseThrow(
        notificationEvent.getId(),
        "not found"
    );

    assertThat(actual.getFiredAt()).isNotEqualTo(expected);
  }

  @Test
  void 미리_등록된_알림_이벤트가_없는_경우_알림_발송을_실패한다() {
    // given
    long invalidId = NotificationEventFixture.getRandomId();

    // when
    ThrowingCallable send = () -> sendNotificationEventUseCase.send(invalidId);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(send)
        .withMessage(NotificationEventErrorCode.NOTIFICATION_EVENT_NOT_EXISTING.getCodeName());
  }

  @Test
  void 알림_이벤트에_연동된_투두가_없는_경우_알림_발송을_실패한다() {
    // given
    long invalidId = NotificationEventFixture.getRandomId();
    Reminder orphanReminder = reminderCommandPort.save(
        ReminderFixture.createReminderWithUserIdAndTodoId(user.getId(), invalidId)
    );
    NotificationEvent eventWithInvalidTodo = notificationEventCommandPort.save(
        NotificationEventFixture.createValidTodoEventNowWithUserAndContext(
            user.getId(),
            orphanReminder.getId()
        )
    );

    // when
    ThrowingCallable send = () -> sendNotificationEventUseCase.send(eventWithInvalidTodo.getId());

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(send)
        .withMessage(NotificationEventErrorCode.ORIGINAL_TODO_NOT_EXISTING.getCodeName());
  }

  @Test
  void 알림_이벤트에_연동된_리마인더가_없는_경우_알림_발송을_실패한다() {
    // given
    NotificationEvent eventWithInvalidReminder = notificationEventCommandPort.save(
        NotificationEventFixture.createValidTodoEventNowWithUserAndContext(
            user.getId(),
            NotificationEventFixture.getRandomId()
        )
    );

    // when
    ThrowingCallable send = () -> sendNotificationEventUseCase.send(
        eventWithInvalidReminder.getId()
    );

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(send)
        .withMessage(ReminderErrorCode.REMINDER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 디바이스_토큰이_없으면_알림_발송에_실패한다() {
    // given
    // TODO: device token 삭제 구현 후 단순화
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal anotherGoal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(anotherUser.getId()));
    LocalDateTime scheduledAt = LocalDateTime.now()
        .plusSeconds(2);
    LocalDateTime remindAt = LocalDateTime.now()
        .plusSeconds(1);
    Todo anotherTodo = saveTodoPort.save(TodoFixture.createTodoWithReminder(
        anotherUser.getId(),
        anotherGoal.getId(),
        scheduledAt.toLocalDate(),
        scheduledAt.toLocalTime(),
        remindAt
    ));
    Reminder anotherReminder = reminderCommandPort.save(
        ReminderFixture.createReminderWithUserIdAndTodoId(anotherUser.getId(), anotherTodo.getId())
    );
    NotificationEvent eventWithoutToken = notificationEventCommandPort.save(
        NotificationEventFixture.createValidTodoEventNowWithUserAndContext(
            anotherUser.getId(),
            anotherReminder.getId()
        )
    );

    // when
    ThrowingCallable send = () -> sendNotificationEventUseCase.send(eventWithoutToken.getId());

    // then
    assertThatExceptionOfType(NotImplementedException.class).isThrownBy(send);
  }

  @Test
  void 지원되지_않는_타입이면_알림_발송에_실패한다() {
    // given
    NotificationEvent unsupported = notificationEventCommandPort.save(
        NotificationEventFixture.createValidEventNowWithUserAndContext(
            user.getId(),
            NotificationEventTypeCode.TEMPLATE_COMMENT,
            reminder.getId()
        )
    );

    // when
    ThrowingCallable send = () -> sendNotificationEventUseCase.send(unsupported.getId());

    // then
    assertThatExceptionOfType(NotImplementedException.class).isThrownBy(send);
  }

}
