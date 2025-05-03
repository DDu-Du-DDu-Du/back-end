package com.ddudu.application.planning.periodgoal.port.in;

import com.ddudu.application.planning.periodgoal.dto.request.UpdatePeriodGoalRequest;

public interface UpdatePeriodGoalUseCase {

  Long update(Long userId, Long id, UpdatePeriodGoalRequest request);

}
