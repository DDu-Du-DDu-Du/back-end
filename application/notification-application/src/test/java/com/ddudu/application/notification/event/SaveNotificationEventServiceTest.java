package com.ddudu.application.notification.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.notification.event.NotificationEventSaveEvent;
import com.ddudu.application.common.dto.notification.event.NotificationScheduleEvent;
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
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
@RecordApplicationEvents
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
    // events are cleared per-test by the framework when using @RecordApplicationEvents
  }

  @Test
  void 알림_이벤트_기록을_성공적으로_저장한다() {
    // given
    NotificationEventSaveEvent saveEvent = NotificationEventSaveEvent.builder()
        .userId(user.getId())
        .contextId(ddudu.getId())
        .typeCode(NotificationEventTypeCode.DDUDU_REMINDER)
        .willFireAt(willFireAt)
        .build();

    // when
    saveNotificationEventUseCase.save(saveEvent);

    // then
    Optional<NotificationEvent> actual = notificationEventLoaderPort.getOptionalEventByContext(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
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
        .typeCode(NotificationEventTypeCode.DDUDU_REMINDER)
        .willFireAt(willFireAt)
        .build();

    // when
    saveNotificationEventUseCase.save(saveEvent);

    // then
    NotificationEvent actual = notificationEventLoaderPort.getOptionalEventByContext(
            user.getId(),
            NotificationEventTypeCode.DDUDU_REMINDER,
            ddudu.getId()
        )
        .orElseThrow();

    assertThat(actual.getId()).isEqualTo(savedNotificationEvent.getId());
    assertThat(actual.getWillFireAt()).isEqualTo(willFireAt);
  }

  @Test
  void 오늘_발송_예정이면_스케줄_이벤트를_발행한다(ApplicationEvents events) {
    // given
    NotificationEventSaveEvent saveEvent = buildSaveEventPlannedToday(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        ddudu.getId()
    );

    // when
    saveNotificationEventUseCase.save(saveEvent);

    // then
    NotificationEvent persisted = notificationEventLoaderPort.getOptionalEventByContext(
            user.getId(),
            NotificationEventTypeCode.DDUDU_REMINDER,
            ddudu.getId())
        .orElseThrow();

    assertThat(events.stream(NotificationScheduleEvent.class)).hasSize(1);
    NotificationScheduleEvent published = events.stream(NotificationScheduleEvent.class)
        .findFirst().orElseThrow();
    assertThat(published.eventId()).isEqualTo(persisted.getId());
    assertThat(published.willFireAt()).isEqualTo(saveEvent.willFireAt());
  }

  @Test
  void 오늘이_아니면_스케줄_이벤트를_발행하지_않는다(ApplicationEvents events) {
    // given
    NotificationEventSaveEvent saveEvent = buildSaveEventPlannedAnotherDay(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        ddudu.getId()
    );

    // when
    saveNotificationEventUseCase.save(saveEvent);

    // then
    assertThat(events.stream(NotificationScheduleEvent.class)).isEmpty();
  }

  @Test
  void 기존_이벤트를_오늘로_수정하면_스케줄_이벤트를_발행한다(ApplicationEvents events) {
    // given: first save for another day
    NotificationEventSaveEvent first = buildSaveEventPlannedAnotherDay(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        ddudu.getId()
    );
    saveNotificationEventUseCase.save(first);
    assertThat(events.stream(NotificationScheduleEvent.class)).isEmpty();

    NotificationEvent beforeUpdate = notificationEventLoaderPort.getOptionalEventByContext(
            user.getId(),
            NotificationEventTypeCode.DDUDU_REMINDER,
            ddudu.getId())
        .orElseThrow();

    // when: update to today
    NotificationEventSaveEvent second = buildSaveEventPlannedToday(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        ddudu.getId()
    );
    saveNotificationEventUseCase.save(second);

    // then
    NotificationEvent afterUpdate = notificationEventLoaderPort.getOptionalEventByContext(
            user.getId(),
            NotificationEventTypeCode.DDUDU_REMINDER,
            ddudu.getId())
        .orElseThrow();
    assertThat(afterUpdate.getId()).isEqualTo(beforeUpdate.getId());
    assertThat(events.stream(NotificationScheduleEvent.class)).hasSize(1);
  }

  @Test
  void 기존_이벤트를_다른_날짜로_수정하면_스케줄_이벤트를_추가_발행하지_않는다(ApplicationEvents events) {
    // given: first save for today
    NotificationEventSaveEvent first = buildSaveEventPlannedToday(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        ddudu.getId()
    );
    saveNotificationEventUseCase.save(first);
    long before = events.stream(NotificationScheduleEvent.class).count();
    assertThat(before).isEqualTo(1);

    // when: update to not today
    NotificationEventSaveEvent second = buildSaveEventPlannedAnotherDay(
        user.getId(),
        NotificationEventTypeCode.DDUDU_REMINDER,
        ddudu.getId()
    );
    saveNotificationEventUseCase.save(second);

    // then
    long after = events.stream(NotificationScheduleEvent.class).count();
    assertThat(after).isEqualTo(before);
  }

  private NotificationEventSaveEvent buildSaveEventPlannedToday(
      Long userId,
      NotificationEventTypeCode typeCode,
      Long contextId
  ) {
    return NotificationEventSaveEvent.builder()
        .userId(userId)
        .typeCode(typeCode)
        .contextId(contextId)
        .willFireAt(LocalDateTime.now().plusSeconds(5))
        .build();
  }

  private NotificationEventSaveEvent buildSaveEventPlannedAnotherDay(
      Long userId,
      NotificationEventTypeCode typeCode,
      Long contextId
  ) {
    return NotificationEventSaveEvent.builder()
        .userId(userId)
        .typeCode(typeCode)
        .contextId(contextId)
        .willFireAt(java.time.LocalDate.now().plusDays(1).atStartOfDay())
        .build();
  }

}
