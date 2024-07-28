package com.ddudu.application.port.in.period_goal;

import com.ddudu.application.dto.period_goal.response.PeriodGoalSummary;
import java.time.LocalDate;

public interface RetrievePeriodGoalUseCase {

  PeriodGoalSummary retrieve(Long userId, LocalDate date, String type);

}
