package com.ddudu.fixture;

import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.NotificationInbox;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationInboxFixture extends BaseFixture {

  public static NotificationInbox createNotReadInboxWithContentFromNotificationEvent(
      NotificationEvent event,
      String title,
      String body
  ) {
    return createNotReadInboxOfUserBySenderWithContextAndContent(
        event.getId(),
        event.getReceiverId(),
        event.getSenderId(),
        event.getTypeCode(),
        event.getContextId(),
        title,
        body
    );
  }

  public static NotificationInbox createNotReadDduduReminderInboxWithContent(
      Long eventId,
      Long userId,
      Long dduduId,
      String title,
      String body
  ) {
    return createNotReadInboxSelfWithContextAndContent(
        eventId,
        userId,
        NotificationEventTypeCode.DDUDU_REMINDER,
        dduduId,
        title,
        body
    );
  }

  public static NotificationInbox createNotReadInboxSelfWithContextAndContent(
      Long eventId,
      Long userId,
      NotificationEventTypeCode typeCode,
      Long contextId,
      String title,
      String body
  ) {
    return createNotReadInboxOfUserBySenderWithContextAndContent(
        eventId,
        userId,
        userId,
        typeCode,
        contextId,
        title,
        body
    );
  }

  public static NotificationInbox createNotReadInboxOfUserBySenderWithContextAndContent(
      Long eventId,
      Long userId,
      Long senderId,
      NotificationEventTypeCode typeCode,
      Long contextId,
      String title,
      String body
  ) {
    return createReadInboxOfUserBySenderWithContextAndContent(
        eventId,
        userId,
        senderId,
        typeCode,
        contextId,
        title,
        body,
        null
    );
  }

  public static NotificationInbox createReadInboxOfUserBySenderWithContextAndContent(
      Long eventId,
      Long userId,
      Long senderId,
      NotificationEventTypeCode typeCode,
      Long contextId,
      String title,
      String body,
      LocalDateTime readAt
  ) {
    long id = getRandomId();

    return createNotificationInbox(
        id,
        userId,
        senderId,
        typeCode,
        contextId,
        title,
        body,
        eventId,
        readAt
    );
  }

  public static NotificationInbox createNotificationInbox(
      Long id,
      Long userId,
      Long senderId,
      NotificationEventTypeCode typeCode,
      Long contextId,
      String title,
      String body,
      Long eventId,
      LocalDateTime readAt
  ) {
    return NotificationInbox.builder()
        .id(id)
        .userId(userId)
        .senderId(senderId)
        .typeCode(typeCode)
        .contextId(contextId)
        .title(title)
        .body(body)
        .eventId(eventId)
        .readAt(readAt)
        .build();
  }

}
