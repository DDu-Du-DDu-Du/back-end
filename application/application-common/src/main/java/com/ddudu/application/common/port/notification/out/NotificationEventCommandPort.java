package com.ddudu.application.common.port.notification.out;

import com.ddudu.domain.notification.event.aggregate.NotificationEvent;

public interface NotificationEventCommandPort {

  NotificationEvent save(NotificationEvent event);

  NotificationEvent update(NotificationEvent event);

  void delete(NotificationEvent event);

}
