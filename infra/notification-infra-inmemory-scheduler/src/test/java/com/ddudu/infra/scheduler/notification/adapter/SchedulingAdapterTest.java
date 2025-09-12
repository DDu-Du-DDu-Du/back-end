package com.ddudu.infra.scheduler.notification.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.ddudu.application.common.dto.notification.event.NotificationSendEvent;
import com.ddudu.infra.scheduler.notification.config.SchedulingTestConfig;
import com.ddudu.infra.scheduler.notification.repository.ScheduleRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@RecordApplicationEvents
@ContextConfiguration(classes = SchedulingTestConfig.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
class SchedulingAdapterTest {

  @Autowired
  SchedulingAdapter schedulingAdapter;

  @Autowired
  ApplicationEvents applicationEvents;

  @Autowired
  TaskScheduler taskScheduler;

  @Autowired
  ScheduleRepository scheduleRepository;

  @Autowired
  ApplicationEventPublisher applicationEventPublisher;

  Long eventId;

  @BeforeEach
  void setUp() {
    eventId = 1L;
  }

  @Test
  void 스케쥴링된_시간에_알림_전송_이벤트를_발행한다() {
    // given
    LocalDateTime willFireAt = LocalDateTime.now()
        .plusSeconds(1);

    // when
    schedulingAdapter.scheduleNotificationEvent(eventId, willFireAt);

    // then
    await().pollInterval(900, TimeUnit.MILLISECONDS)
        .atMost(2, TimeUnit.SECONDS)
        .untilAsserted(() -> {
          boolean actual = applicationEvents.stream(NotificationSendEvent.class)
              .anyMatch(event -> event.eventId()
                  .equals(eventId));

          assertThat(actual).isTrue();
        });
  }

  @Test
  void 스케쥴러에_등록된_알림_이벤트_전송_예약을_취소한다() {
    // given
    Instant willFireAt = Instant.now()
        .plusSeconds(10);
    NotificationSendEvent sendEvent = new NotificationSendEvent(eventId);
    ScheduledFuture<?> schedule = taskScheduler.schedule(
        () -> applicationEventPublisher.publishEvent(sendEvent),
        willFireAt
    );

    scheduleRepository.save(eventId, schedule);

    // when
    schedulingAdapter.cancelNotificationEvent(eventId);

    // then
    assertThat(schedule.isCancelled()).isTrue();
  }

}