package com.ddudu.application.common.dto.stats;

import lombok.Builder;

@Builder
public record CreationCountPerGoalDto(Long goalId, int count) {

  public static CreationCountPerGoalDto from(Long goalId, int count) {
    return CreationCountPerGoalDto.builder()
        .goalId(goalId)
        .count(count)
        .build();
  }

}
