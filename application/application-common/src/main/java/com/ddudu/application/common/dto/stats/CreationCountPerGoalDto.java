package com.ddudu.application.common.dto.stats;

import com.ddudu.aggregate.MonthlyStats;
import lombok.Builder;

@Builder
public record CreationCountPerGoalDto(Long goalId, String goalName, int count) {

  public static CreationCountPerGoalDto from(MonthlyStats monthlyStats) {
    return CreationCountPerGoalDto.builder()
        .goalId(monthlyStats.getGoalId())
        .goalName(monthlyStats.getGoalName())
        .count(monthlyStats.size())
        .build();
  }

}
