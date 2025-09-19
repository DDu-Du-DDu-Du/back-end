package com.ddudu.application.notification.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.notification.event.NotificationSendEvent;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.notification.in.SendNotificationEventUseCase;
import com.ddudu.application.common.port.notification.out.NotificationDeviceTokenCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.common.exception.NotificationEventErrorCode;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.NotificationDeviceTokenFixture;
import com.ddudu.fixture.NotificationEventFixture;
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
  SaveDduduPort saveDduduPort;

  @Autowired
  NotificationEventCommandPort notificationEventCommandPort;

  @Autowired
  NotificationEventLoaderPort notificationEventLoaderPort;

  @Autowired
  NotificationDeviceTokenCommandPort notificationDeviceTokenCommandPort;

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

    notificationDeviceTokenCommandPort.save(NotificationDeviceTokenFixture.createWithUser(user.getId()));
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
    Ddudu anotherDdudu = saveDduduPort.save(DduduFixture.createDduduWithReminder(
        anotherUser.getId(),
        anotherGoal.getId(),
        scheduledAt.toLocalDate(),
        scheduledAt.toLocalTime(),
        remindAt
    ));
    NotificationEvent eventWithoutToken = notificationEventCommandPort.save(
        NotificationEventFixture.createValidDduduEventNowWithUserAndContext(
            anotherUser.getId(),
            anotherDdudu.getId()
        )
    );

    // when
    ThrowingCallable send = () -> sendNotificationEventUseCase.send(
        new NotificationSendEvent(eventWithoutToken.getId())
    );

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
            ddudu.getId()
        )
    );

    // when
    ThrowingCallable send = () -> sendNotificationEventUseCase.send(
        new NotificationSendEvent(unsupported.getId())
    );

    // then
    assertThatExceptionOfType(NotImplementedException.class).isThrownBy(send);
  }

}
