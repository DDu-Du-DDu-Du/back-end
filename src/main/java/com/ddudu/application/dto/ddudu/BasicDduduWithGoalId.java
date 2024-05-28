package com.ddudu.application.dto.ddudu;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import lombok.Builder;

@Builder
public record BasicDduduWithGoalId(
    Long id,
    String name,
    DduduStatus status,
    Long goalId
) {

  public static BasicDduduWithGoalId of(Ddudu ddudu) {
    return BasicDduduWithGoalId.builder()
        .id(ddudu.getId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .goalId(ddudu.getGoalId())
        .build();
  }

}
