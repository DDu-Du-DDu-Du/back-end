package com.modoo.application.common.dto.reminder.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UpdateReminderRequest(
    @NotNull(message = "2103 NULL_REMINDS_AT")
    LocalDateTime remindsAt
) {

}
