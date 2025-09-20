package com.ddudu.application.common.dto.notification.event;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationScheduleEvent(Long eventId, LocalDateTime willFireAt) {

}
