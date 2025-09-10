package com.ddudu.application.common.dto.notification.event;

import com.ddudu.application.common.dto.interim.InterimNotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
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
