package com.ddudu.infra.inmemory.notification.adapter;

import com.ddudu.application.common.dto.notification.event.NotificationScheduleEvent;
import com.ddudu.application.common.port.notification.out.NotificationSchedulingPort;
import com.ddudu.application.common.port.notification.out.ScheduleCommandPort;
import com.ddudu.application.common.port.notification.out.ScheduleLoaderPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.infra.inmemory.notification.repository.ScheduleRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@DrivenAdapter
@RequiredArgsConstructor
public class SchedulingAdapter implements NotificationSchedulingPort, ScheduleCommandPort,
    ScheduleLoaderPort {

  private final ApplicationEventPublisher applicationEventPublisher;
  private final ScheduleRepository scheduleRepository;

  @Override
  public void scheduleNotificationEvent(Long eventId, LocalDateTime willFireAt) {
    NotificationScheduleEvent event = NotificationScheduleEvent.builder()
        .eventId(eventId)
        .willFireAt(willFireAt)
        .build();

    applicationEventPublisher.publishEvent(event);
  }

  @Override
  public void cancelNotificationEvent(Long eventId) {
    ScheduledFuture<?> event = scheduleRepository.delete(eventId);

    event.cancel(false);
  }

  @Override
  public Optional<ScheduledFuture<?>> getOptionalSchedule(Long eventId) {
    return scheduleRepository.find(eventId);
  }

  @Override
  public void save(Long eventId, ScheduledFuture<?> schedule) {
    scheduleRepository.save(eventId, schedule);
  }

}
