package com.ddudu.application.common.dto.stats;

import com.ddudu.aggregate.MonthlyStats;
import java.time.YearMonth;
import lombok.Builder;

@Builder
public record MonthlyStatsReportDto(
    YearMonth yearMonth,
    int totalCount,
    int achievementRate,
    int sustenanceCount,
    int postponementCount,
    int reattainmentRate
) {

  public static MonthlyStatsReportDto from(MonthlyStats stats) {
    return MonthlyStatsReportDto.builder()
        .yearMonth(stats.getYearMonth())
        .totalCount(stats.size())
        .achievementRate(stats.calculateAchievementRate())
        .sustenanceCount(stats.calculateSustenanceCount())
        .postponementCount(stats.calculatePostponementCount())
        .reattainmentRate(stats.calculateReattainmentRate())
        .build();
  }

}
