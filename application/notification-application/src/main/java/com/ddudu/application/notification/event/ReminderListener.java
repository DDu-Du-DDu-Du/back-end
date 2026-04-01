package com.ddudu.application.notification.event;

import com.ddudu.application.common.dto.interim.InterimCancelReminderEvent;
import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventSaveEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReminderListener {

  private final ApplicationEventPublisher applicationEventPublisher;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishSaveNotificationEvent(InterimSetReminderEvent event) {
    NotificationEventSaveEvent saveEvent = NotificationEventSaveEvent.from(event);
    applicationEventPublisher.publishEvent(saveEvent);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void publishRemoveNotificationEvent(InterimCancelReminderEvent event) {
    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.from(event);
    applicationEventPublisher.publishEvent(removeEvent);
  }

}
