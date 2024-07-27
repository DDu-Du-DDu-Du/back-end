package com.ddudu.application.port.in.period_goal;

import com.ddudu.application.dto.period_goal.request.UpdatePeriodGoalRequest;

public interface UpdatePeriodGoalUseCase {

  Long update(Long userId, Long id, UpdatePeriodGoalRequest request);

}
