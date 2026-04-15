package com.modoo.application.common.dto.notification.response;

import java.time.LocalDateTime;

public record AnnouncementDetailResponse(
    Long id,
    String title,
    String body,
    LocalDateTime createdAt,
    String author
) {

}
