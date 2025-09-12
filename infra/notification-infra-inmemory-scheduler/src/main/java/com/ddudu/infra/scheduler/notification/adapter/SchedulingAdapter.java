package com.ddudu.infra.scheduler.notification.adapter;

import com.ddudu.application.common.dto.notification.event.NotificationSendEvent;
import com.ddudu.application.common.port.notification.out.NotificationSchedulingPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.infra.scheduler.notification.repository.ScheduleRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;

@DrivenAdapter
@RequiredArgsConstructor
public class SchedulingAdapter implements NotificationSchedulingPort {

  private final TaskScheduler taskScheduler;
  private final ScheduleRepository scheduleRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public void scheduleNotificationEvent(Long eventId, LocalDateTime willFireAt) {
    scheduleRepository.find(eventId)
        .ifPresent(schedule -> schedule.cancel(false));

    Instant instant = willFireAt.atZone(ZoneId.systemDefault())
        .toInstant();

    ScheduledFuture<?> schedule = taskScheduler.schedule(
        () -> publishNotificationSendEvent(eventId),
        instant
    );

    scheduleRepository.save(eventId, schedule);
  }

  @Override
  public void cancelNotificationEvent(Long eventId) {
    ScheduledFuture<?> schedule = scheduleRepository.delete(eventId);

    schedule.cancel(false);
  }

  private void publishNotificationSendEvent(Long eventId) {
    applicationEventPublisher.publishEvent(new NotificationSendEvent(eventId));
    scheduleRepository.delete(eventId);
  }

}
