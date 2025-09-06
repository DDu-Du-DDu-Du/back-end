package com.ddudu.domain.notification.event.aggregate;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.common.exception.NotificationEventErrorCode;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
public class NotificationEvent {

  @EqualsAndHashCode.Include
  private final Long id;
  private final NotificationEventTypeCode typeCode;
  private final Long senderId;
  private final Long receiverId;
  private final Long contextId;
  private final LocalDateTime willFireAt;
  private final LocalDateTime firedAt;

  @Builder
  public NotificationEvent(
      Long id,
      NotificationEventTypeCode typeCode,
      Long senderId,
      Long receiverId,
      Long contextId,
      LocalDateTime willFireAt,
      LocalDateTime firedAt
  ) {
    validate(typeCode, receiverId, contextId);

    this.id = id;
    this.typeCode = typeCode;
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.contextId = contextId;
    this.willFireAt = Objects.requireNonNullElse(willFireAt, LocalDateTime.now());
    this.firedAt = firedAt;
  }

  public boolean isAlreadyFired() {
    return Objects.nonNull(firedAt);
  }

  private void validate(NotificationEventTypeCode typeCode, Long receiverId, Long contextId) {
    checkArgument(
        Objects.nonNull(typeCode),
        NotificationEventErrorCode.NULL_TYPE_CODE.getCodeName()
    );
    checkArgument(
        Objects.nonNull(receiverId),
        NotificationEventErrorCode.NULL_RECEIVER_ID.getCodeName()
    );
    checkArgument(
        Objects.nonNull(contextId),
        NotificationEventErrorCode.NULL_CONTEXT_ID.getCodeName()
    );
  }

}
