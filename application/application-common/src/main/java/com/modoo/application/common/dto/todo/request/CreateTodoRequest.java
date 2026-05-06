package com.modoo.application.common.dto.todo.request;

import com.modoo.domain.planning.todo.dto.CreateTodoCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateTodoRequest(
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
    List<CreateTodoReminderRequest> reminders
) {

  public CreateTodoCommand toCommand() {
    return CreateTodoCommand.builder()
        .goalId(goalId)
        .name(name)
        .memo(memo)
        .scheduledOn(scheduledOn)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

}
