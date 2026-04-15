package com.modoo.application.common.port.notification.out;

import com.modoo.domain.notification.event.aggregate.NotificationEvent;

public interface NotificationEventCommandPort {

  NotificationEvent save(NotificationEvent event);

  NotificationEvent update(NotificationEvent event);

  void delete(NotificationEvent event);

}
