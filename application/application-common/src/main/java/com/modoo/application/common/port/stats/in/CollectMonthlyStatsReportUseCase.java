package com.modoo.application.common.port.stats.in;

import com.modoo.application.common.dto.stats.response.MonthlyStatsReportResponse;
import java.time.YearMonth;

public interface CollectMonthlyStatsReportUseCase {

  MonthlyStatsReportResponse collectReport(Long loginId, YearMonth yearMonth);

}
