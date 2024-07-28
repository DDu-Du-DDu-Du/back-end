package com.ddudu.application.port.in.period_goal;

import com.ddudu.application.dto.period_goal.request.CreatePeriodGoalRequest;

public interface CreatePeriodGoalUseCase {

  Long create(Long userId, CreatePeriodGoalRequest request);

}
