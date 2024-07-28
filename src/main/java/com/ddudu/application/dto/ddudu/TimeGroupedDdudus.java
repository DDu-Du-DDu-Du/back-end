package com.ddudu.application.dto.ddudu;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record TimeGroupedDdudus(
    @Schema(type = "string", pattern = "HH:mm", example = "14:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime beginAt,
    List<BasicDduduWithGoalIdAndTime> ddudus
) {

  public static TimeGroupedDdudus of(LocalTime beginAt, List<BasicDduduWithGoalIdAndTime> ddudus) {
    return TimeGroupedDdudus.builder()
        .beginAt(beginAt)
        .ddudus(ddudus)
        .build();
  }

}
