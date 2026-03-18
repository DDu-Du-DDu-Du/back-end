package com.ddudu.fixture;

import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationEventFixture extends BaseFixture {

  public static NotificationEvent createFiredTodoEventNowWithUserAndContext(
      Long receiverId,
      Long todoId
  ) {
    LocalDateTime nearNow = LocalDateTime.now()
        .plusSeconds(10);

    return createFiredTodoEventWithUserAndContext(receiverId, todoId, nearNow);
  }

  public static NotificationEvent createValidTodoEventNowWithUserAndContext(
      Long receiverId,
      Long todoId
  ) {
    LocalDateTime nearNow = LocalDateTime.now()
        .plusSeconds(10);

    return createValidTodoEventWithUserAndContext(receiverId, todoId, nearNow);
  }

  public static NotificationEvent createFiredTodoEventWithUserAndContext(
      Long receiverId,
      Long todoId,
      LocalDateTime willFireAt
  ) {
    return createFiredEventWithUserAndContext(
        receiverId,
        NotificationEventTypeCode.TODO_REMINDER,
        todoId,
        willFireAt
    );
  }

  public static NotificationEvent createValidTodoEventWithUserAndContext(
      Long receiverId,
      Long todoId,
      LocalDateTime willFireAt
  ) {
    return createValidEventWithUserAndContext(
        receiverId,
        NotificationEventTypeCode.TODO_REMINDER,
        todoId,
        willFireAt
    );
  }

  public static NotificationEvent createFiredEventNowWithUserAndContext(
      Long receiverId,
      NotificationEventTypeCode typeCode,
      Long contextId
  ) {
    LocalDateTime nearNow = LocalDateTime.now()
        .plusSeconds(10);

    return createFiredEventWithUserAndContext(receiverId, typeCode, contextId, nearNow);
  }

  public static NotificationEvent createFiredEventWithUserAndContext(
      Long receiverId,
      NotificationEventTypeCode typeCode,
      Long contextId,
      LocalDateTime willFireAt
  ) {
    LocalDateTime firedAt = getPastDateTime(10, TimeUnit.DAYS);

    return createNotificationEventWithUserAndContext(
        receiverId,
        typeCode,
        contextId,
        willFireAt,
        firedAt
    );
  }

  public static NotificationEvent createValidEventNowWithUserAndContext(
      Long receiverId,
      NotificationEventTypeCode typeCode,
      Long contextId
  ) {
    LocalDateTime nearNow = LocalDateTime.now()
        .plusSeconds(10);

    return createValidEventWithUserAndContext(receiverId, typeCode, contextId, nearNow);
  }

  public static NotificationEvent createValidEventWithUserAndContext(
      Long receiverId,
      NotificationEventTypeCode typeCode,
      Long contextId,
      LocalDateTime willFireAt
  ) {
    return createNotificationEventWithUserAndContext(
        receiverId,
        typeCode,
        contextId,
        willFireAt,
        null
    );
  }

  public static NotificationEvent createNotificationEventWithUserAndContext(
      Long receiverId,
      NotificationEventTypeCode typeCode,
      Long contextId,
      LocalDateTime willFireAt,
      LocalDateTime firedAt
  ) {
    return createNotificationEvent(
        typeCode,
        receiverId,
        receiverId,
        contextId,
        willFireAt,
        firedAt
    );
  }

  public static NotificationEvent createNotificationEvent(
      NotificationEventTypeCode typeCode,
      Long senderId,
      Long receiverId,
      Long contextId,
      LocalDateTime willFireAt,
      LocalDateTime firedAt
  ) {
    return NotificationEvent.builder()
        .typeCode(typeCode)
        .senderId(senderId)
        .receiverId(receiverId)
        .contextId(contextId)
        .willFireAt(willFireAt)
        .firedAt(firedAt)
        .build();
  }

}
