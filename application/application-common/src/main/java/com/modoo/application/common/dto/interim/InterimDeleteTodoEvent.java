package com.modoo.application.common.dto.interim;

import com.modoo.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.modoo.domain.planning.todo.aggregate.Todo;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record InterimDeleteTodoEvent(
    Long userId,
    NotificationEventTypeCode typeCode,
    Long contextId,
    LocalDateTime willFireAt
) implements InterimNotificationEvent {

  public static InterimDeleteTodoEvent from(Long userId, Todo todo) {
    return InterimDeleteTodoEvent.builder()
        .userId(userId)
        .typeCode(NotificationEventTypeCode.TODO_REMINDER)
        .contextId(todo.getId())
        .build();
  }

}
