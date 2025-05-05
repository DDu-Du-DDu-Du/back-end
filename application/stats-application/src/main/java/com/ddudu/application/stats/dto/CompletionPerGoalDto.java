package com.ddudu.application.stats.dto;

import lombok.Builder;

@Builder
public record CompletionPerGoalDto(Long goalId, int count) {

  public static CompletionPerGoalDto from(Long goalId, int count) {
    return CompletionPerGoalDto.builder()
        .goalId(goalId)
        .count(count)
        .build();
  }

}
