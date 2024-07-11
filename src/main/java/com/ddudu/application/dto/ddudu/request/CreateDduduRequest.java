package com.ddudu.application.dto.ddudu.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreateDduduRequest(
    @NotNull(message = "2001 NULL_GOAL_VALUE")
    @Positive(message = "2014 NEGATIVE_OR_ZERO_GOAL_ID")
    Long goalId,
    @NotBlank(message = "2002 BLANK_NAME")
    @Size(max = 50, message = "2003 EXCESSIVE_NAME_LENGTH")
    String name,
    @NotNull(message = "2015 NULL_SCHEDULED_DATE")
    LocalDate scheduledOn
) {

}
