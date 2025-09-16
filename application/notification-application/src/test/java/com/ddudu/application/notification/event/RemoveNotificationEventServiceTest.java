package com.ddudu.application.notification.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.notification.in.RemoveNotificationEventUseCase;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.NotificationEventFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
class RemoveNotificationEventServiceTest {

  @Autowired
  RemoveNotificationEventUseCase removeNotificationEventUseCase;

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
  LocalDateTime willFireAt;
  NotificationEvent notificationEvent;

  @BeforeEach
  void setUp() {
    willFireAt = NotificationEventFixture.getFutureDateTime(10, TimeUnit.DAYS);
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
    notificationEvent = NotificationEventFixture.createValidDduduEventWithUserAndContext(
        user.getId(),
        ddudu.getId(),
        willFireAt
    );
  }

  @Test
  void 알림_이벤트_기록을_성공적으로_삭제한다() {
    // given
    notificationEventCommandPort.save(notificationEvent);

    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.builder()
        .userId(user.getId())
        .typeCode(NotificationEventTypeCode.DDUDU_REMINDER)
        .contextId(ddudu.getId())
        .build();
    boolean expected = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        ddudu.getId()
    );

    // when
    removeNotificationEventUseCase.remove(removeEvent);

    // then
    boolean actual = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        ddudu.getId()
    );

    assertThat(actual).isNotEqualTo(expected);
  }

  @Test
  void 알림이_존재하지_않으면_삭제하지_않는다() {
    // given
    long invalidId = NotificationEventFixture.getRandomId();
    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.builder()
        .userId(user.getId())
        .typeCode(NotificationEventTypeCode.DDUDU_REMINDER)
        .contextId(invalidId)
        .build();
    boolean expected = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        invalidId
    );

    // when
    removeNotificationEventUseCase.remove(removeEvent);

    // then
    boolean actual = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        invalidId
    );

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void 알림이_이미_발송됐으면_삭제하지_않는다() {
    // given
    notificationEvent = notificationEventCommandPort.save(
        NotificationEventFixture.createFiredDduduEventNowWithUserAndContext(
            user.getId(),
            ddudu.getId()
        )
    );
    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.builder()
        .userId(user.getId())
        .typeCode(NotificationEventTypeCode.DDUDU_REMINDER)
        .contextId(ddudu.getId())
        .build();
    boolean expected = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        ddudu.getId()
    );

    // when
    removeNotificationEventUseCase.remove(removeEvent);

    // then
    boolean actual = notificationEventLoaderPort.existsByContext(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        ddudu.getId()
    );

    assertThat(actual).isEqualTo(expected);
  }

}