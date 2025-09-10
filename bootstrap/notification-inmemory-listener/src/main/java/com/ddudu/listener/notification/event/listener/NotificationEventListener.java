package com.ddudu.listener.notification.event.listener;

import com.ddudu.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventSaveEvent;
import com.ddudu.application.common.port.notification.in.RemoveNotificationEventUseCase;
import com.ddudu.application.common.port.notification.in.SaveNotificationEventUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final SaveNotificationEventUseCase saveNotificationEventUseCase;
  private final RemoveNotificationEventUseCase removeNotificationEventUseCase;

  @EventListener
  public void saveNotificationEvent(NotificationEventSaveEvent notificationEventSaveEvent) {
    saveNotificationEventUseCase.save(notificationEventSaveEvent);
  }

  @EventListener
  public void removeNotificationEvent(NotificationEventRemoveEvent notificationEventRemoveEvent) {
    removeNotificationEventUseCase.remove(notificationEventRemoveEvent);
  }

}
