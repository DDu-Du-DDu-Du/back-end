package com.ddudu.application.common.dto.stats;

import com.ddudu.aggregate.MonthlyStats;
import java.time.YearMonth;
import lombok.Builder;

@Builder
public record MonthlyStatsSummaryDto(
    YearMonth yearMonth,
    int totalCount,
    int achievementPercentage,
    int sustenanceCount,
    int postponementCount,
    int reattainmentPercentage
) {

  public static MonthlyStatsSummaryDto from(MonthlyStats stats) {
    return MonthlyStatsSummaryDto.builder()
        .yearMonth(stats.getYearMonth())
        .totalCount(stats.size())
        .achievementPercentage(stats.calculateAchievementPercentage())
        .sustenanceCount(stats.calculateSustenanceCount())
        .postponementCount(stats.calculatePostponementCount())
        .reattainmentPercentage(stats.calculateReattainmentCount())
        .build();
  }

}
