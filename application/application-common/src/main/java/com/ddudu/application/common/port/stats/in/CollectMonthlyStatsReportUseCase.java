package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.response.MonthlyStatsReportResponse;
import java.time.YearMonth;

public interface CollectMonthlyStatsReportUseCase {

  MonthlyStatsReportResponse collectReport(Long loginId, YearMonth yearMonth);

}
