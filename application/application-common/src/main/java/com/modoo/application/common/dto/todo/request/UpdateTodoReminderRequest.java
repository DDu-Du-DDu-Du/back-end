package com.modoo.application.common.dto.todo.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record UpdateTodoReminderRequest(
    Long id,
    @NotNull(message = "2101 NULL_REMINDS_AT")
    LocalDateTime remindsAt
) {

}
