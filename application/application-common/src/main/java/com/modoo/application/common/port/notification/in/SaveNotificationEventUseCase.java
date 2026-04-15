package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.notification.event.NotificationEventSaveEvent;

public interface SaveNotificationEventUseCase {

  void save(NotificationEventSaveEvent event);

}
