package com.ddudu.application.notification.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.notification.event.NotificationEventSaveEvent;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.notification.in.SaveNotificationEventUseCase;
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
import java.util.Optional;
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
class SaveNotificationEventServiceTest {

  @Autowired
  SaveNotificationEventUseCase saveNotificationEventUseCase;

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

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
    willFireAt = NotificationEventFixture.getFutureDateTime(10, TimeUnit.DAYS);
  }

  @Test
  void 알림_이벤트_기록을_성공적으로_저장한다() {
    // given
    NotificationEventSaveEvent saveEvent = NotificationEventSaveEvent.builder()
        .userId(user.getId())
        .contextId(ddudu.getId())
        .typeCode(NotificationEventTypeCode.DDUDU)
        .willFireAt(willFireAt)
        .build();

    // when
    saveNotificationEventUseCase.save(saveEvent);

    // then
    Optional<NotificationEvent> actual = notificationEventLoaderPort.getOptionalEventByContext(
        user.getId(),
        NotificationEventTypeCode.DDUDU,
        ddudu.getId()
    );

    assertThat(actual).isPresent();
    assertThat(actual.get()
        .getWillFireAt()).isEqualTo(willFireAt);
  }

  @Test
  void 이미_존재하는_알림_컨텍스트면_발송_예정_시간을_수정한다() {
    // given
    NotificationEvent savedNotificationEvent = notificationEventCommandPort.save(
        NotificationEventFixture.createValidDduduEventNowWithUserAndContext(
            user.getId(),
            ddudu.getId()
        )
    );

    NotificationEventSaveEvent saveEvent = NotificationEventSaveEvent.builder()
        .userId(user.getId())
        .contextId(ddudu.getId())
        .typeCode(NotificationEventTypeCode.DDUDU)
        .willFireAt(willFireAt)
        .build();

    // when
    saveNotificationEventUseCase.save(saveEvent);

    // then
    NotificationEvent actual = notificationEventLoaderPort.getOptionalEventByContext(
            user.getId(),
            NotificationEventTypeCode.DDUDU,
            ddudu.getId()
        )
        .orElseThrow();

    assertThat(actual.getId()).isEqualTo(savedNotificationEvent.getId());
    assertThat(actual.getWillFireAt()).isEqualTo(willFireAt);
  }

}