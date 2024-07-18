package com.ddudu.application.dto.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record BasicDduduWithGoalIdAndTime(
    Long id,
    String name,
    DduduStatus status,
    Long goalId,
    @Schema(type = "string", pattern = "HH:mm", example = "14:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime beginAt,
    @Schema(type = "string", pattern = "HH:mm", example = "14:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime endAt
) {

  public static BasicDduduWithGoalIdAndTime of(Ddudu ddudu) {

    return BasicDduduWithGoalIdAndTime.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .goalId(ddudu.getGoalId())
        .beginAt(ddudu.getBeginAt())
        .endAt(ddudu.getEndAt())
        .build();
  }

}
