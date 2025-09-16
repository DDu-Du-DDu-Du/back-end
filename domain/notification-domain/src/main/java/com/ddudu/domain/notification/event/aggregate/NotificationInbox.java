package com.ddudu.domain.notification.event.aggregate;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.common.exception.NotificationInboxErrorCode;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class NotificationInbox {

  private static final int MAX_TITLE_LENGTH = 50;
  private static final int MAX_BODY_LENGTH = 200;

  private final Long id;
  private final Long userId;
  private final Long senderId;
  private final Long eventId;
  private final Long contextId;
  private final NotificationEventTypeCode typeCode;
  private final String title;
  private final String body;
  private final LocalDateTime readAt;

  @Builder
  private NotificationInbox(
      Long id,
      Long userId,
      Long senderId,
      Long eventId,
      Long contextId,
      NotificationEventTypeCode typeCode,
      String title,
      String body,
      LocalDateTime readAt
  ) {
    validate(userId, eventId, typeCode, contextId, title, body);

    this.id = id;
    this.userId = userId;
    this.senderId = senderId;
    this.eventId = eventId;
    this.contextId = contextId;
    this.typeCode = typeCode;
    this.title = title;
    this.body = body;
    this.readAt = readAt;
  }

  private void validate(
      Long userId,
      Long eventId,
      NotificationEventTypeCode typeCode,
      Long contextId,
      String title,
      String body
  ) {
    checkArgument(
        Objects.nonNull(userId),
        NotificationInboxErrorCode.NULL_USER_ID.getCodeName()
    );
    checkArgument(
        Objects.nonNull(eventId),
        NotificationInboxErrorCode.NULL_EVENT_ID.getCodeName()
    );
    checkArgument(
        Objects.nonNull(typeCode),
        NotificationInboxErrorCode.NULL_TYPE_CODE.getCodeName()
    );
    checkArgument(
        Objects.nonNull(contextId),
        NotificationInboxErrorCode.NULL_CONTEXT_ID.getCodeName()
    );

    validateTitle(title);

    if (StringUtils.isNotBlank(body)) {
      checkArgument(
          body.length() <= MAX_BODY_LENGTH,
          NotificationInboxErrorCode.EXCESSIVE_BODY_LENGTH.getCodeName()
      );
    }
  }

  private void validateTitle(String title) {
    checkArgument(
        Objects.nonNull(title),
        NotificationInboxErrorCode.NULL_TITLE.getCodeName()
    );
    checkArgument(
        title.length() <= MAX_TITLE_LENGTH,
        NotificationInboxErrorCode.EXCESSIVE_TITLE_LENGTH.getCodeName()
    );
  }

}
