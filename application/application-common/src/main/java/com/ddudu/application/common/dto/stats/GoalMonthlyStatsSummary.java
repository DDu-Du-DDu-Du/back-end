package com.ddudu.application.common.dto.stats;

import lombok.Builder;

@Builder
public record GoalMonthlyStatsSummary(
    Long goalId,
    String goalName,
    String goalColor,
    int creationCount,
    int achievementCount,
    int postponedCount,
    int sustainedCount,
    int reattainedCount
) {

}
