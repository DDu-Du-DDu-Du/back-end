package com.ddudu.application.common.dto.todo;

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

  public static TodoForTimetable of(Todo todo, String color) {
    return TodoForTimetable.builder()
        .id(todo.getId())
        .name(todo.getName())
        .status(todo.getStatus())
        .goalId(todo.getGoalId())
        .color(color)
        .postponedAt(todo.getPostponedAt())
        .beginAt(todo.getBeginAt())
        .endAt(todo.getEndAt())
        .build();
  }

}
