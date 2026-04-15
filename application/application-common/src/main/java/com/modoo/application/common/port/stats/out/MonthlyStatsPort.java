package com.modoo.application.common.port.stats.out;

import com.modoo.aggregate.MonthlyStats;
import com.modoo.application.common.dto.stats.RepeatTodoStatsDto;
import com.modoo.domain.planning.goal.aggregate.Goal;
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

  Map<YearMonth, MonthlyStats> collectMonthlyPostponedStats(
      Long userId,
      Goal goal,
      LocalDate from,
      LocalDate to
  );

  List<RepeatTodoStatsDto> countRepeatTodo(
      Long userId,
      Long goalId,
      LocalDate from,
      LocalDate to
  );

}
