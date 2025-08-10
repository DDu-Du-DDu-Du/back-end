package com.ddudu.application.common.port.stats.out;

import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.application.common.dto.stats.RepeatDduduStatsDto;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public interface MonthlyStatsPort {

  Map<YearMonth, MonthlyStats> collectMonthlyStats(
      Long userId,
      Goal goal,
      LocalDate from,
      LocalDate to
  );

  List<RepeatDduduStatsDto> countRepeatDdudu(
      Long userId,
      Long goalId,
      LocalDate from,
      LocalDate to
  );

}
