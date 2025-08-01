package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import java.time.YearMonth;

public interface CollectMonthlyStatsSummaryUseCase {

  MonthlyStatsSummaryResponse collectSummary(Long loginId, YearMonth yearMonth);

}
