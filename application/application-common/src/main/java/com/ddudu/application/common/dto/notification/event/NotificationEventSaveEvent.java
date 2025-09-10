package com.ddudu.application.common.dto.notification.event;

import com.ddudu.application.common.dto.interim.InterimNotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationEventSaveEvent(
    Long userId,
    NotificationEventTypeCode typeCode,
    Long contextId,
    LocalDateTime willFireAt
) {

  public static NotificationEventSaveEvent from(InterimNotificationEvent event) {
    return NotificationEventSaveEvent.builder()
        .userId(event.userId())
        .typeCode(event.typeCode())
        .contextId(event.contextId())
        .willFireAt(event.willFireAt())
        .build();
  }

}
