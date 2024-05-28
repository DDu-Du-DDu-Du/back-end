package com.ddudu.application.dto.ddudu;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;

@Builder
public record TimeGroupedDdudus(
    @Schema(type = "string", pattern = "HH:mm", example = "14:00")
    LocalTime beginAt,
    List<BasicDduduWithGoalId> ddudus
) {

  public static TimeGroupedDdudus of(LocalTime beginAt, List<BasicDduduWithGoalId> ddudus) {
    return TimeGroupedDdudus.builder()
        .beginAt(beginAt)
        .ddudus(ddudus)
        .build();
  }

}
