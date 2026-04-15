package com.modoo.listener.notification.event.listener;

import com.modoo.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.modoo.application.common.dto.notification.event.NotificationEventSaveEvent;
import com.modoo.application.common.port.notification.in.RemoveNotificationEventUseCase;
import com.modoo.application.common.port.notification.in.SaveNotificationEventUseCase;
import com.modoo.application.common.port.notification.in.SendNotificationEventUseCase;
import com.modoo.common.util.ListenerLogAction;
import com.modoo.common.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

  private static final String LOG_TYPE = "NOTI_EVENT_RCVD";

  private final SaveNotificationEventUseCase saveNotificationEventUseCase;
  private final RemoveNotificationEventUseCase removeNotificationEventUseCase;
  private final SendNotificationEventUseCase sendNotificationEventUseCase;

  @EventListener
  public void saveNotificationEvent(NotificationEventSaveEvent notificationEventSaveEvent) {
    long startMs = logStart(notificationEventSaveEvent.contextId());

    try {
      saveNotificationEventUseCase.save(notificationEventSaveEvent);
    } catch (Exception e) {
      logException(startMs, notificationEventSaveEvent.contextId(), e);

      throw e;
    }

    logEnd(startMs, notificationEventSaveEvent.contextId());
  }

  @EventListener
  public void removeNotificationEvent(NotificationEventRemoveEvent notificationEventRemoveEvent) {
    long startMs = logStart(notificationEventRemoveEvent.contextId());

    try {
      removeNotificationEventUseCase.remove(notificationEventRemoveEvent);
    } catch (Exception e) {
      logException(startMs, notificationEventRemoveEvent.contextId(), e);

      throw e;
    }

    logEnd(startMs, notificationEventRemoveEvent.contextId());
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
