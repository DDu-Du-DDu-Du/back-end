package com.ddudu.scheduler.notification.scheduler;

import com.ddudu.application.common.port.notification.in.PersistScheduleUseCase;
import com.ddudu.application.common.port.notification.in.ScheduleTomorrowRemindersUseCase;
import com.ddudu.application.common.port.notification.in.SendNotificationEventUseCase;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

  private final TaskScheduler taskScheduler;
  private final PersistScheduleUseCase persistScheduleUseCase;
  private final SendNotificationEventUseCase sendNotificationEventUseCase;
  private final ScheduleTomorrowRemindersUseCase scheduleTomorrowRemindersUseCase;

  public void registerSchedule(Long eventId, LocalDateTime willFireAt) {
    Instant instant = willFireAt
        .atZone(ZoneId.systemDefault())
        .toInstant();
    ScheduledFuture<?> schedule = taskScheduler.schedule(
        () -> sendNotificationEventUseCase.send(eventId),
        instant
    );

    persistScheduleUseCase.persistSchedule(eventId, schedule);
  }

  @Scheduled(cron = "0 55 23 * * *")
  public void registerAllReminderNextDay() {
    scheduleTomorrowRemindersUseCase.registerAllTomorrowReminders();
  }

}
