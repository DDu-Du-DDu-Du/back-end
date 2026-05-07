package com.modoo.domain.planning.todo.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;
import lombok.Builder;

@Builder
public record CreateTodoCommand(
    Long goalId,
    String name,
    String memo,
    LocalDate scheduledOn,
    LocalTime beginAt,
    LocalTime endAt,
    ZoneId clientTimeZone
) {

  public CreateTodoCommand {
    clientTimeZone = Objects.requireNonNullElse(clientTimeZone, ZoneOffset.UTC);
  }

}
