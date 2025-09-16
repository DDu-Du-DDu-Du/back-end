package com.ddudu.fixture;

import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationEventFixture extends BaseFixture {

  public static NotificationEvent createFiredDduduEventNowWithUserAndContext(
      Long receiverId,
      Long dduduId
  ) {
    LocalDateTime nearNow = LocalDateTime.now()
        .plusSeconds(10);

    return createFiredDduduEventWithUserAndContext(receiverId, dduduId, nearNow);
  }

  public static NotificationEvent createValidDduduEventNowWithUserAndContext(
      Long receiverId,
      Long dduduId
  ) {
    LocalDateTime nearNow = LocalDateTime.now()
        .plusSeconds(10);

    return createValidDduduEventWithUserAndContext(receiverId, dduduId, nearNow);
  }

  public static NotificationEvent createFiredDduduEventWithUserAndContext(
      Long receiverId,
      Long dduduId,
      LocalDateTime willFireAt
  ) {
    return createFiredEventWithUserAndContext(
        receiverId,
        NotificationEventTypeCode.DDUDU_REMINDER,
        dduduId,
        willFireAt
    );
  }

  public static NotificationEvent createValidDduduEventWithUserAndContext(
      Long receiverId,
      Long dduduId,
      LocalDateTime willFireAt
  ) {
    return createValidEventWithUserAndContext(
        receiverId,
        NotificationEventTypeCode.DDUDU_REMINDER,
        dduduId,
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
