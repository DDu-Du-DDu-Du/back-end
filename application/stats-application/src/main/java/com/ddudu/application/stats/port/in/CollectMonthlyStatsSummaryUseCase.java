package com.ddudu.application.stats.port.in;

import com.ddudu.application.stats.dto.response.MonthlyStatsSummaryResponse;
import java.time.YearMonth;

public interface CollectMonthlyStatsSummaryUseCase {

  MonthlyStatsSummaryResponse collectMonthlyTotalStats(
      Long loginId, YearMonth yearMonth
  );

}
