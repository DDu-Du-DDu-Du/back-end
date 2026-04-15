package com.modoo.domain.planning.todo.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record CreateTodoCommand(
    Long goalId,
    String name,
    String memo,
    LocalDate scheduledOn,
    LocalTime beginAt,
    LocalTime endAt
) {

}
