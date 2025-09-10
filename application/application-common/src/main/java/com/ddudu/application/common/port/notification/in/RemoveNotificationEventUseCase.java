package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.notification.event.NotificationEventRemoveEvent;

public interface RemoveNotificationEventUseCase {

  void remove(NotificationEventRemoveEvent event);

}
