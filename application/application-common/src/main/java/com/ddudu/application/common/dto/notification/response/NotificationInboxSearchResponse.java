package com.ddudu.application.common.dto.notification.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationInboxSearchResponse(
    Long id,
    Long senderId,
    boolean isFromSystem,
    String context,
    Long contextId,
    boolean isRead,
    LocalDateTime createdAt,
    String title,
    String body
) {

}
