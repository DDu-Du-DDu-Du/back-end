package com.ddudu.application.common.dto.stats;

import com.ddudu.aggregate.MonthlyStats;
import lombok.Builder;

@Builder
public record PostponedPerGoal(Long goalId, String goalName, int postponementCount) {

  public static PostponedPerGoal from(MonthlyStats monthlyStats) {
    return PostponedPerGoal.builder()
        .goalId(monthlyStats.getGoalId())
        .goalName(monthlyStats.getGoalName())
        .postponementCount(monthlyStats.calculatePostponementCount())
        .build();
  }

}
