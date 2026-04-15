package com.modoo.application.common.dto.notification;

import com.modoo.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDateTime;

public record NotificationInboxSearchDto(
    Long id,
    Long senderId,
    NotificationEventTypeCode typeCode,
    Long contextId,
    LocalDateTime readAt,
    LocalDateTime createdAt,
    String title,
    String body
) {

}
