package com.modoo.application.common.dto.todo.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MoveDateRequest(
    @NotNull(message = "2011 NULL_DATE_TO_MOVE")
    LocalDate newDate,
    boolean postpone
) {

}
