package com.ddudu.application.common.port.stats.out;

import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

public interface MonthlyStatsPort {

  Map<YearMonth, MonthlyStats> collectMonthlyStats(
      Long userId,
      Goal goal,
      LocalDate from,
      LocalDate to
  );

}
