package com.ddudu.application.common.dto.interim;

import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDateTime;

public interface InterimNotificationEvent {

  Long userId();

  NotificationEventTypeCode typeCode();

  Long contextId();

  LocalDateTime willFireAt();

}
