package com.ddudu.application.notification.schedule;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.port.notification.in.PersistScheduleUseCase;
import com.ddudu.application.common.port.notification.out.ScheduleLoaderPort;
import com.ddudu.fixture.NotificationEventFixture;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
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
class PersistScheduleServiceTest {

  @Autowired
  PersistScheduleUseCase persistScheduleUseCase;

  @Autowired
  ScheduleLoaderPort scheduleLoaderPort;

  ScheduledThreadPoolExecutor executor;

  @BeforeEach
  void setUp() {
    executor = new ScheduledThreadPoolExecutor(1);
  }

  @AfterEach
  void tearDown() {
    executor.shutdownNow();
  }

  @Test
  void 기존_스케줄이_없으면_새로운_스케줄을_저장한다() {
    // given
    Long eventId = NotificationEventFixture.getRandomId();
    ScheduledFuture<?> expected = executor.schedule(() -> { }, 10, TimeUnit.MINUTES);

    // when
    persistScheduleUseCase.persistSchedule(eventId, expected);

    // then
    ScheduledFuture<?> actual = scheduleLoaderPort.getOptionalSchedule(eventId)
        .orElseThrow();

    assertThat(actual.isCancelled()).isFalse();
    assertThat(Objects.equals(actual, expected)).isTrue();
  }

  @Test
  void 기존_스케줄이_있으면_기존을_취소하고_새로운_스케줄로_교체한다() {
    // given
    Long eventId = NotificationEventFixture.getRandomId();
    ScheduledFuture<?> first = executor.schedule(() -> { }, 10, TimeUnit.MINUTES);

    persistScheduleUseCase.persistSchedule(eventId, first);

    ScheduledFuture<?> second = executor.schedule(() -> { }, 20, TimeUnit.MINUTES);

    // when
    persistScheduleUseCase.persistSchedule(eventId, second);

    // then
    ScheduledFuture<?> actual = scheduleLoaderPort.getOptionalSchedule(eventId)
        .orElseThrow();

    assertThat(Objects.equals(actual, second)).isTrue();
    assertThat(first.isCancelled()).isTrue();
    assertThat(second.isCancelled()).isFalse();
  }
}

