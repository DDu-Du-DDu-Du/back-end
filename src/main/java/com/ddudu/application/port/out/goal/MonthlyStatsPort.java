package com.ddudu.application.port.out.goal;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.StatsBaseDto;
import java.time.LocalDate;
import java.util.List;

public interface MonthlyStatsPort {

  List<StatsBaseDto> collectMonthlyStats(
      User user, Goal goal, LocalDate from, LocalDate to
  );

}
