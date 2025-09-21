package com.ddudu.application.common.dto.notification;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReminderScheduleTargetDto(Long eventId, LocalDateTime willFireAt) {

}
