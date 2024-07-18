package com.ddudu.application.dto.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import java.time.LocalTime;
import lombok.Builder;

@Builder
public record BasicDduduWithGoalIdAndTime(
    Long id,
    String name,
    DduduStatus status,
    Long goalId,
    LocalTime beginAt,
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
