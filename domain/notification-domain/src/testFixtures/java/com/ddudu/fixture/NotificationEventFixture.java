package com.ddudu.fixture;

import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationEventFixture extends BaseFixture {

  private static NotificationEvent createFiredDduduEventNowWithUserAndContext(
      Long receiverId,
      Long dduduId
  ) {
    return createFiredDduduEventWithUserAndContext(receiverId, dduduId, LocalDateTime.now());
  }

  public static NotificationEvent createValidDduduEventNowWithUserAndContext(
      Long receiverId,
      Long dduduId
  ) {
    return createValidDduduEventWithUserAndContext(receiverId, dduduId, LocalDateTime.now());
  }

  public static NotificationEvent createFiredDduduEventWithUserAndContext(
      Long receiverId,
      Long dduduId,
      LocalDateTime willFireAt
  ) {
    return createFiredEventWithUserAndContext(
        receiverId,
        NotificationEventTypeCode.DDUDU,
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
        NotificationEventTypeCode.DDUDU,
        dduduId,
        willFireAt
    );
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
