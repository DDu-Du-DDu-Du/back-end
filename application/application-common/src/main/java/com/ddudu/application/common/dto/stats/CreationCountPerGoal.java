package com.ddudu.application.common.dto.stats;

import com.ddudu.aggregate.MonthlyStats;
import lombok.Builder;

@Builder
public record CreationCountPerGoal(Long goalId, String goalName, int count) {

  public static CreationCountPerGoal from(MonthlyStats monthlyStats) {
    return CreationCountPerGoal.builder()
        .goalId(monthlyStats.getGoalId())
        .goalName(monthlyStats.getGoalName())
        .count(monthlyStats.size())
        .build();
  }

}
