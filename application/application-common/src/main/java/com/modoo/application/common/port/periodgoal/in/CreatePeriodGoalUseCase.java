package com.modoo.application.common.port.periodgoal.in;

import com.modoo.application.common.dto.periodgoal.request.CreatePeriodGoalRequest;

public interface CreatePeriodGoalUseCase {

  Long create(Long userId, CreatePeriodGoalRequest request);

}
