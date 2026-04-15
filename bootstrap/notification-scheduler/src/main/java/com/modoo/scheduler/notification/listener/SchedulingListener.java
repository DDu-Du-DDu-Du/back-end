package com.modoo.scheduler.notification.listener;

import com.modoo.application.common.dto.notification.event.NotificationScheduleEvent;
import com.modoo.common.util.ListenerLogAction;
import com.modoo.common.util.LogUtil;
import com.modoo.scheduler.notification.scheduler.NotificationScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulingListener {

  private static final String LOG_TYPE = "NOTI_SCHEDULE";

  private final NotificationScheduler notificationScheduler;

  @EventListener
  public void listenScheduleEvent(NotificationScheduleEvent event) {
    long startMs = logStart(event.eventId());

    try {
      notificationScheduler.registerSchedule(event.eventId(), event.willFireAt());
    } catch (Exception e) {
      logException(startMs, event.eventId(), e);

      throw e;
    }

    logEnd(startMs, event.eventId());
  }

  private long logStart(Long contextId) {
    long start = System.currentTimeMillis();

    log.info("{} type={} referenceId={}", ListenerLogAction.START.prefix(), LOG_TYPE, contextId);

    return start;
  }

  private void logException(long startMs, Long contextId, Exception e) {
    long durationMs = System.currentTimeMillis() - startMs;
    String exceptionSimpleName = e.getClass()
        .getSimpleName();

    log.info(
        "{} type={} referenceId={} durationMs={} exception={} message={}",
        ListenerLogAction.ERR.prefix(),
        LOG_TYPE,
        contextId,
        durationMs,
        exceptionSimpleName,
        e.getMessage(),
        e
    );
  }

  private void logEnd(long startMs, Long contextId) {
    long durationMs = System.currentTimeMillis() - startMs;
    String prefix = ListenerLogAction.END.prefix();

    if (LogUtil.isSlow(durationMs)) {
      prefix = ListenerLogAction.SLOW.prefix();
    }

    log.info("{} type={} referenceId={} durationMs={}", prefix, LOG_TYPE, contextId, durationMs);
  }

}
