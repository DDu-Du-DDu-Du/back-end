package com.ddudu.application.dto.ddudu.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record MoveDateRequest(
    @NotNull(message = "2011 NULL_DATE_TO_MOVE")
    LocalDate newDate
) {

}
