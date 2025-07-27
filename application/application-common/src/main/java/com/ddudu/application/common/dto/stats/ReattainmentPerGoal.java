package com.ddudu.application.common.dto.stats;

import com.ddudu.aggregate.MonthlyStats;
import lombok.Builder;

@Builder
public record ReattainmentPerGoal(
    Long goalId,
    String goalName,
    Integer reattainmentRate
) {

  public static ReattainmentPerGoal from(MonthlyStats monthlyStats) {
    return ReattainmentPerGoal.builder()
        .goalId(monthlyStats.getGoalId())
        .goalName(monthlyStats.getGoalName())
        .reattainmentRate(monthlyStats.calculateReattainmentRate())
        .build();
  }

}
