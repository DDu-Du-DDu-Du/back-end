package com.ddudu.application.planning.goal.port.out;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.stats.dto.StatsBaseDto;
import java.time.LocalDate;
import java.util.List;

public interface MonthlyStatsPort {

  List<StatsBaseDto> collectMonthlyStats(
      User user, Goal goal, LocalDate from, LocalDate to
  );

}
