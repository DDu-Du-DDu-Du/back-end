package com.ddudu.application.stats.dto.response;

import com.ddudu.application.stats.dto.MonthlyStatsSummaryDto;
import lombok.Builder;

@Builder
public record MonthlyStatsSummaryResponse(
    MonthlyStatsSummaryDto lastMonth,
    MonthlyStatsSummaryDto thisMonth
) {

  public static MonthlyStatsSummaryResponse from(
      MonthlyStatsSummaryDto lastMonth, MonthlyStatsSummaryDto thisMonth
  ) {
    return MonthlyStatsSummaryResponse.builder()
        .lastMonth(lastMonth)
        .thisMonth(thisMonth)
        .build();
  }

}
