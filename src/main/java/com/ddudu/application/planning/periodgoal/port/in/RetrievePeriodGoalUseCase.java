package com.ddudu.application.planning.periodgoal.port.in;

import com.ddudu.application.planning.periodgoal.dto.response.PeriodGoalSummary;
import java.time.LocalDate;

public interface RetrievePeriodGoalUseCase {

  PeriodGoalSummary retrieve(Long userId, LocalDate date, String type);

}
