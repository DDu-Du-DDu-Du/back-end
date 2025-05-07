package com.ddudu.application.port.periodgoal.in;

import com.ddudu.application.dto.periodgoal.response.PeriodGoalSummary;
import java.time.LocalDate;

public interface RetrievePeriodGoalUseCase {

  PeriodGoalSummary retrieve(Long userId, LocalDate date, String type);

}
