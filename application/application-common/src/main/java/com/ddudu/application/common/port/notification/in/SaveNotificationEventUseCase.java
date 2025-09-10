package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.notification.event.NotificationEventSaveEvent;

public interface SaveNotificationEventUseCase {

  void save(NotificationEventSaveEvent event);

}
