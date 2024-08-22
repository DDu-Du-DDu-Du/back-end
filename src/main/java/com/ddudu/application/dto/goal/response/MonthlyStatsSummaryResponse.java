package com.ddudu.application.dto.goal.response;

import com.ddudu.application.dto.goal.MonthlyStatsSummaryDto;
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
