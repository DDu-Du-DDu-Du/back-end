package com.ddudu.application.common.dto.ddudu.request;

import com.ddudu.domain.planning.ddudu.dto.CreateDduduCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record CreateDduduRequest(
    @NotNull(message = "2001 NULL_GOAL_VALUE")
    @Positive(message = "2014 NEGATIVE_OR_ZERO_GOAL_ID")
    Long goalId,
    @NotBlank(message = "2002 BLANK_NAME")
    @Size(
        max = 50,
        message = "2003 EXCESSIVE_NAME_LENGTH"
    )
    String name,
    @Size(
        max = 2000,
        message = "2023 EXCESSIVE_MEMO_LENGTH"
    )
    String memo,
    @NotNull(message = "2015 NULL_SCHEDULED_DATE")
    LocalDate scheduledOn,
    LocalTime beginAt,
    LocalTime endAt,
    @PositiveOrZero(message = "2020 NEGATIVE_REMINDER_INPUT_EXISTS")
    Integer remindDays,
    @PositiveOrZero(message = "2020 NEGATIVE_REMINDER_INPUT_EXISTS")
    Integer remindHours,
    @PositiveOrZero(message = "2020 NEGATIVE_REMINDER_INPUT_EXISTS")
    Integer remindMinutes
) {

  public CreateDduduCommand toCommand() {
    return CreateDduduCommand.builder()
        .goalId(goalId)
        .name(name)
        .memo(memo)
        .scheduledOn(scheduledOn)
        .beginAt(beginAt)
        .endAt(endAt)
        .remindDays(remindDays)
        .remindHours(remindHours)
        .remindMinutes(remindMinutes)
        .build();
  }

}
