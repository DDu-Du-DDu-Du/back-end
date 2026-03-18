package com.ddudu.application.common.dto.ddudu;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record TimeGroupedTodos(
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
    List<TodoForTimetable> ddudus
) {

  public static TimeGroupedTodos of(LocalTime beginAt, List<TodoForTimetable> ddudus) {
    return TimeGroupedTodos.builder()
        .beginAt(beginAt)
        .ddudus(ddudus)
        .build();
  }

}
