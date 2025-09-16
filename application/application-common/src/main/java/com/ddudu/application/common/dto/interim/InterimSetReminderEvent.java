package com.ddudu.application.common.dto.interim;

import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InterimSetReminderEvent(
    Long userId,
    NotificationEventTypeCode typeCode,
    Long contextId,
    LocalDateTime willFireAt
) implements InterimNotificationEvent {

  public static InterimSetReminderEvent from(Long userId, Ddudu ddudu) {
    return InterimSetReminderEvent.builder()
        .userId(userId)
        .typeCode(NotificationEventTypeCode.DDUDU_REMINDER)
        .contextId(ddudu.getId())
        .willFireAt(ddudu.getRemindAt())
        .build();
  }

}
