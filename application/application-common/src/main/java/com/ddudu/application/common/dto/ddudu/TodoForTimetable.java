package com.ddudu.application.common.dto.ddudu;

import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record TodoForTimetable(
    Long id,
    String name,
    TodoStatus status,
    Long goalId,
    String color,
    LocalDateTime postponedAt,
    @Schema(
        type = "string",
        pattern = "HH:mm",
        example = "14:00"
    )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "HH:mm"
    )
    LocalTime beginAt,
    @Schema(
        type = "string",
        pattern = "HH:mm",
        example = "14:30"
    )
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "HH:mm"
    )
    LocalTime endAt
) {

  public static TodoForTimetable of(Todo ddudu, String color) {
    return TodoForTimetable.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .goalId(ddudu.getGoalId())
        .color(color)
        .postponedAt(ddudu.getPostponedAt())
        .beginAt(ddudu.getBeginAt())
        .endAt(ddudu.getEndAt())
        .build();
  }

}
