package com.ddudu.application.dto.stats;

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

  public static MonthlyStatsSummaryDto of(
      YearMonth yearMonth, int totalCount, int achievementPercentage, int sustenanceCount,
      int postponementCount, int reattainmentPercentage
  ) {
    return MonthlyStatsSummaryDto.builder()
        .yearMonth(yearMonth)
        .totalCount(totalCount)
        .achievementPercentage(achievementPercentage)
        .sustenanceCount(sustenanceCount)
        .postponementCount(postponementCount)
        .reattainmentPercentage(reattainmentPercentage)
        .build();
  }

}
