package com.modoo.scheduler.notification.scheduler;

import com.modoo.application.common.port.notification.in.PersistScheduleUseCase;
import com.modoo.application.common.port.notification.in.ScheduleTomorrowRemindersUseCase;
import com.modoo.application.common.port.notification.in.SendNotificationEventUseCase;
import com.modoo.common.util.LogUtil;
import com.modoo.common.util.SchedulerLogAction;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

  private final TaskScheduler taskScheduler;
  private final PersistScheduleUseCase persistScheduleUseCase;
  private final SendNotificationEventUseCase sendNotificationEventUseCase;
  private final ScheduleTomorrowRemindersUseCase scheduleTomorrowRemindersUseCase;

  public void registerSchedule(Long eventId, LocalDateTime willFireAt) {
    log.info(
        "{} referenceId={} willTriggerAt={}",
        SchedulerLogAction.REG.prefix(),
        eventId,
        willFireAt
    );

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
    log.info("{} {}", SchedulerLogAction.BATCH.prefix(), "Daily Reminder Registration START");
    scheduleTomorrowRemindersUseCase.registerAllTomorrowReminders();
  }

  private void logScheduler(Long eventId, LocalDateTime scheduledAt, Instant triggerAt) {
    long start = System.currentTimeMillis();
    String scheduleId = UUID.randomUUID()
        .toString();
    long delayMs = start - triggerAt.toEpochMilli();

    log.info(
        "{} scheduleId={} referenceId={} scheduledAt={} delayMs={}",
        SchedulerLogAction.TRIG.prefix(),
        scheduleId,
        eventId,
        scheduledAt,
        delayMs
    );

    try {
      sendNotificationEventUseCase.send(eventId);

      long durationMs = System.currentTimeMillis() - start;
      String prefix = SchedulerLogAction.TRIG.prefix();

      if (LogUtil.isSlow(durationMs)) {
        prefix = SchedulerLogAction.SLOW.prefix();
      }

      log.info(
          "{} scheduleId={} referenceId={} durationMs={}",
          prefix,
          scheduleId,
          eventId,
          durationMs
      );
    } catch (Exception e) {
      String exceptionSimpleName = e.getClass()
          .getSimpleName();
      long durationMs = System.currentTimeMillis() - start;

      log.error(
          "{} scheduleId={} referenceId={} durationMs={} exception={} message={}",
          SchedulerLogAction.ERR.prefix(),
          scheduleId,
          eventId,
          durationMs,
          exceptionSimpleName,
          e.getMessage(),
          e
      );

      throw e;
    }
  }

}
