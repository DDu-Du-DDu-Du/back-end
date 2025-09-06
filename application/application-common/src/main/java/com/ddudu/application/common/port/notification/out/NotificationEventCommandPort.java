package com.ddudu.application.common.port.notification.out;

import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;

public interface NotificationEventCommandPort {

  NotificationEvent save(NotificationEvent event);

  void delete(NotificationEvent event);

  void deleteAllByContext(NotificationEventTypeCode typeCode, Long contextId);

}
