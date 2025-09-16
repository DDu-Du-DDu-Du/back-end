package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.notification.event.NotificationSendEvent;

public interface SendNotificationEventUseCase {

  void send(NotificationSendEvent event);

}
