package com.ddudu.application.dto.ddudu;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record DduduForTimetable(
    Long id,
    String name,
    DduduStatus status,
    Long goalId,
    String color,
    @Schema(type = "string", pattern = "HH:mm", example = "14:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime beginAt,
    @Schema(type = "string", pattern = "HH:mm", example = "14:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime endAt
) {

  public static DduduForTimetable of(Ddudu ddudu, String color) {
    return DduduForTimetable.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .goalId(ddudu.getGoalId())
        .color(color)
        .beginAt(ddudu.getBeginAt())
        .endAt(ddudu.getEndAt())
        .build();
  }

}
