package com.ddudu.application.port.periodgoal.in;

import com.ddudu.application.dto.periodgoal.request.UpdatePeriodGoalRequest;

public interface UpdatePeriodGoalUseCase {

  Long update(Long userId, Long id, UpdatePeriodGoalRequest request);

}
