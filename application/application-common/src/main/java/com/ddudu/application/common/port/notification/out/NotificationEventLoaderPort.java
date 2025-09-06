package com.ddudu.application.common.port.notification.out;

import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.util.Optional;

public interface NotificationEventLoaderPort {

  boolean existsByContext(NotificationEventTypeCode typeCode, Long contextId);

  Optional<NotificationEvent> getOptionalEventByContext(
      NotificationEventTypeCode typeCode,
      Long contextId
  );

}
