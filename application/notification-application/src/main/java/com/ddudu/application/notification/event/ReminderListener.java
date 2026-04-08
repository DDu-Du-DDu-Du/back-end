package com.ddudu.application.notification.event;

import com.ddudu.application.common.dto.interim.InterimCancelReminderEvent;
import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventSaveEvent;
import com.ddudu.common.util.ListenerLogAction;
import com.ddudu.common.util.LogUtil;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReminderListener {

  private final ApplicationEventPublisher applicationEventPublisher;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishSaveNotificationEvent(InterimSetReminderEvent event) {
    long start = logStart(event.typeCode(), event.contextId());

    try {
      NotificationEventSaveEvent saveEvent = NotificationEventSaveEvent.from(event);

      applicationEventPublisher.publishEvent(saveEvent);
    } catch (Exception e) {
      logException(start, event.typeCode(), event.contextId(), e);

      throw e;
    }

    logEnd(start, event.typeCode(), event.contextId());
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishRemoveNotificationEvent(InterimCancelReminderEvent event) {
    long start = logStart(event.typeCode(), event.contextId());

    try {
      NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.from(event);

      applicationEventPublisher.publishEvent(removeEvent);
    } catch (Exception e) {
      logException(start, event.typeCode(), event.contextId(), e);

      throw e;
    }

    logEnd(start, event.typeCode(), event.contextId());
  }

  private long logStart(NotificationEventTypeCode type, Long contextId) {
    long start = System.currentTimeMillis();

    log.info("{} type={} referenceId={}", ListenerLogAction.START.prefix(), type, contextId);

    return start;
  }

  private void logException(
      long startMs,
      NotificationEventTypeCode type,
      Long contextId,
      Exception e
  ) {
    long durationMs = System.currentTimeMillis() - startMs;
    String exceptionSimpleName = e.getClass()
        .getSimpleName();

    log.info(
        "{} type={} referenceId={} durationMs={} exception={} message={}",
        ListenerLogAction.ERR.prefix(),
        type,
        contextId,
        durationMs,
        exceptionSimpleName,
        e.getMessage(),
        e
    );
  }

  private void logEnd(
      long startMs,
      NotificationEventTypeCode type,
      Long contextId
  ) {
    long durationMs = System.currentTimeMillis() - startMs;
    String prefix = ListenerLogAction.END.prefix();

    if (LogUtil.isSlow(durationMs)) {
      prefix = ListenerLogAction.SLOW.prefix();
    }

    log.info("{} type={} referenceId={} durationMs={}", prefix, type, contextId, durationMs);
  }

}
