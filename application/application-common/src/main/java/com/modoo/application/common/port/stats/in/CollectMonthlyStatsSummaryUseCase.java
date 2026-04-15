package com.modoo.application.common.port.stats.in;

import com.modoo.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import java.time.YearMonth;

public interface CollectMonthlyStatsSummaryUseCase {

  MonthlyStatsSummaryResponse collectSummary(Long loginId, Long userId, YearMonth yearMonth);

}
