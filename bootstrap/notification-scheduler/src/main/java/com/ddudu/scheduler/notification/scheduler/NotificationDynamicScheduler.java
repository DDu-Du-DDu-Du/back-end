package com.ddudu.scheduler.notification.scheduler;

import com.ddudu.application.common.port.notification.in.PersistScheduleUseCase;
import com.ddudu.application.common.port.notification.in.SendNotificationEventUseCase;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationDynamicScheduler {

  private final TaskScheduler dynamicScheduler;
  private final PersistScheduleUseCase persistScheduleUseCase;
  private final SendNotificationEventUseCase sendNotificationEventUseCase;

  public void registerSchedule(Long eventId, LocalDateTime willFireAt) {
    Instant instant = willFireAt
        .atZone(ZoneId.systemDefault())
        .toInstant();
    ScheduledFuture<?> schedule = dynamicScheduler.schedule(
        () -> sendNotificationEventUseCase.send(eventId),
        instant
    );

    persistScheduleUseCase.persistSchedule(eventId, schedule);
  }

}
