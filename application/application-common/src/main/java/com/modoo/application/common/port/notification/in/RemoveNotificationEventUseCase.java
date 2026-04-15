package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.notification.event.NotificationEventRemoveEvent;

public interface RemoveNotificationEventUseCase {

  void remove(NotificationEventRemoveEvent event);

}
