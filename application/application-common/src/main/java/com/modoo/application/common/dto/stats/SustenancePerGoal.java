package com.modoo.application.common.dto.stats;

import com.modoo.aggregate.MonthlyStats;
import lombok.Builder;

@Builder
public record SustenancePerGoal(Long goalId, String goalName, Integer sustenanceCount) {

  public static SustenancePerGoal from(MonthlyStats monthlyStats) {
    return SustenancePerGoal.builder()
        .goalId(monthlyStats.getGoalId())
        .goalName(monthlyStats.getGoalName())
        .sustenanceCount(monthlyStats.calculateSustenanceCount())
        .build();
  }

}
