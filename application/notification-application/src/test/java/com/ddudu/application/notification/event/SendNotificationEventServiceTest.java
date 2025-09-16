package com.ddudu.application.notification.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.notification.event.NotificationSendEvent;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.notification.in.SendNotificationEventUseCase;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.common.exception.NotificationEventErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.NotificationEventFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDateTime;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;
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
  SaveDduduPort saveDduduPort;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  @Autowired
  NotificationEventLoaderPort notificationEventLoaderPort;

  User user;
  Goal goal;
  Ddudu ddudu;
  NotificationEvent notificationEvent;

  @BeforeEach
  void setUp() {
    LocalDateTime scheduledAt = LocalDateTime.now()
        .plusSeconds(2);
    LocalDateTime remindAt = LocalDateTime.now()
        .plusSeconds(1);
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveDduduPort.save(DduduFixture.createDduduWithReminder(
        user.getId(),
        goal.getId(),
        scheduledAt.toLocalDate(),
        scheduledAt.toLocalTime(),
        remindAt
    ));
    notificationEvent = NotificationEventFixture.createValidDduduEventNowWithUserAndContext(
        user.getId(),
        ddudu.getId()
    );
    notificationEvent = notificationEventCommandPort.save(notificationEvent);
  }

  @Test
  void 알림_발송을_성공한다() {
    // given
    NotificationSendEvent sendEvent = new NotificationSendEvent(notificationEvent.getId());
    LocalDateTime expected = notificationEvent.getFiredAt();

    // when
    sendNotificationEventUseCase.send(sendEvent);

    // then
    NotificationEvent actual = notificationEventLoaderPort.getEventOrElseThrow(
        sendEvent.eventId(),
        "not found"
    );

    assertThat(actual.getFiredAt()).isNotEqualTo(expected);
  }

  @Test
  void 미리_등록된_알림_이벤트가_없는_경우_알림_발송을_실패한다() {
    // given
    long invalidId = NotificationEventFixture.getRandomId();
    NotificationSendEvent invalidEvent = new NotificationSendEvent(invalidId);

    // when
    ThrowingCallable send = () -> sendNotificationEventUseCase.send(invalidEvent);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(send)
        .withMessage(NotificationEventErrorCode.NOTIFICATION_EVENT_NOT_EXISTING.getCodeName());
  }

  @Test
  void 알림_이벤트에_연동된_뚜두가_없는_경우_알림_발송을_실패한다() {
    // given
    long invalidId = NotificationEventFixture.getRandomId();
    NotificationEvent eventWithInvalidDdudu = notificationEventCommandPort.save(
        NotificationEventFixture.createValidDduduEventNowWithUserAndContext(
            user.getId(),
            invalidId
        )
    );
    NotificationSendEvent invalidEvent = new NotificationSendEvent(eventWithInvalidDdudu.getId());

    // when
    ThrowingCallable send = () -> sendNotificationEventUseCase.send(invalidEvent);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(send)
        .withMessage(NotificationEventErrorCode.ORIGINAL_DDUDU_NOT_EXISTING.getCodeName());
  }

}