package com.ddudu.application.planning.periodgoal.port.in;

import com.ddudu.application.planning.periodgoal.dto.request.CreatePeriodGoalRequest;

public interface CreatePeriodGoalUseCase {

  Long create(Long userId, CreatePeriodGoalRequest request);

}
