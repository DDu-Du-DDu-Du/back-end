package com.modoo.application.common.port.periodgoal.in;

import com.modoo.application.common.dto.periodgoal.response.PeriodGoalSummary;
import java.time.LocalDate;

public interface RetrievePeriodGoalUseCase {

  PeriodGoalSummary retrieve(Long userId, LocalDate date, String type);

}
