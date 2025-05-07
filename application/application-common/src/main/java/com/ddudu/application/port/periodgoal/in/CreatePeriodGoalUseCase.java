package com.ddudu.application.port.periodgoal.in;

import com.ddudu.application.dto.periodgoal.request.CreatePeriodGoalRequest;

public interface CreatePeriodGoalUseCase {

  Long create(Long userId, CreatePeriodGoalRequest request);

}
