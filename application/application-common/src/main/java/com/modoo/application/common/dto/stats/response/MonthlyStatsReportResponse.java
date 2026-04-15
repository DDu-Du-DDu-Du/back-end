package com.modoo.application.common.dto.stats.response;

import com.modoo.application.common.dto.stats.MonthlyStatsReportDto;
import lombok.Builder;

@Builder
public record MonthlyStatsReportResponse(
    MonthlyStatsReportDto lastMonth,
    MonthlyStatsReportDto thisMonth
) {

  public static MonthlyStatsReportResponse from(
      MonthlyStatsReportDto lastMonth,
      MonthlyStatsReportDto thisMonth
  ) {
    return MonthlyStatsReportResponse.builder()
        .lastMonth(lastMonth)
        .thisMonth(thisMonth)
        .build();
  }

}
