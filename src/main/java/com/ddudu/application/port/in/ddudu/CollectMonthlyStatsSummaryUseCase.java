package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.dto.stats.response.MonthlyStatsSummaryResponse;
import java.time.YearMonth;

public interface CollectMonthlyStatsSummaryUseCase {

  MonthlyStatsSummaryResponse collectMonthlyTotalStats(
      Long loginId, YearMonth yearMonth
  );

}
