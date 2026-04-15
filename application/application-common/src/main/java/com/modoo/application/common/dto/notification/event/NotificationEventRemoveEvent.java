package com.modoo.application.common.dto.notification.event;

import com.modoo.application.common.dto.interim.InterimNotificationEvent;
import com.modoo.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import lombok.Builder;

@Builder
public record NotificationEventRemoveEvent(
    Long userId,
    NotificationEventTypeCode typeCode,
    Long contextId
) {

  public static NotificationEventRemoveEvent from(InterimNotificationEvent event) {
    return NotificationEventRemoveEvent.builder()
        .userId(event.userId())
        .typeCode(event.typeCode())
        .contextId(event.contextId())
        .build();
  }

}
