package com.ddudu.application.common.port.periodgoal.in;

import com.ddudu.application.common.dto.periodgoal.request.CreatePeriodGoalRequest;

public interface CreatePeriodGoalUseCase {

  Long create(Long userId, CreatePeriodGoalRequest request);

}
