package com.ddudu.application.common.dto.notification;

import java.time.LocalDateTime;

public record SimpleAnnouncementDto(
    Long id,
    String title,
    String author,
    LocalDateTime createdAt
) {

}
