package com.ddudu.domain.planning.todo.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record UpdateTodoCommand(
    Long goalId,
    String name,
    String memo,
    LocalDate scheduledOn,
    LocalTime beginAt,
    LocalTime endAt
) {

}
