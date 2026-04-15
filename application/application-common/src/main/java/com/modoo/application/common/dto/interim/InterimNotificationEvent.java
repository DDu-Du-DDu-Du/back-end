package com.modoo.application.common.dto.interim;

import com.modoo.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDateTime;

public interface InterimNotificationEvent {

  Long userId();

  NotificationEventTypeCode typeCode();

  Long contextId();

  LocalDateTime willFireAt();

}
