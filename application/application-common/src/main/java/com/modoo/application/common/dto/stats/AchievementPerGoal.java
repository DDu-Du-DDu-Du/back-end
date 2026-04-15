package com.modoo.application.common.dto.stats;

import com.modoo.aggregate.MonthlyStats;
import lombok.Builder;

@Builder
public record AchievementPerGoal(Long goalId, String goalName, Integer achievementRate) {

  public static AchievementPerGoal from(MonthlyStats monthlyStats) {
    return AchievementPerGoal.builder()
        .goalId(monthlyStats.getGoalId())
        .goalName(monthlyStats.getGoalName())
        .achievementRate(monthlyStats.calculateAchievementRate())
        .build();
  }

}
