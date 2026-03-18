package com.ddudu.application.common.dto.interim;

import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InterimCancelReminderEvent(
    Long userId,
    NotificationEventTypeCode typeCode,
    Long contextId,
    LocalDateTime willFireAt
) implements InterimNotificationEvent {

  public static InterimCancelReminderEvent from(Long userId, Todo todo) {
    return InterimCancelReminderEvent.builder()
        .userId(userId)
        .typeCode(NotificationEventTypeCode.TODO_REMINDER)
        .contextId(todo.getId())
        .build();
  }

}
