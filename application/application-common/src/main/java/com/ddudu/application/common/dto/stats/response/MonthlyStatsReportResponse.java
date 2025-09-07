package com.ddudu.application.common.dto.stats.response;

import com.ddudu.application.common.dto.stats.MonthlyStatsReportDto;
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
