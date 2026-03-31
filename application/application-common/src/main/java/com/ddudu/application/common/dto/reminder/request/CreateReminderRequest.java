package com.ddudu.application.common.dto.reminder.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record CreateReminderRequest(
    @NotNull(message = "2102 NULL_TODO_VALUE")
    @Positive(message = "2107 TODO_NOT_EXISTING")
    Long todoId,
    @NotNull(message = "2103 NULL_REMINDS_AT")
    LocalDateTime remindsAt
) {

}
