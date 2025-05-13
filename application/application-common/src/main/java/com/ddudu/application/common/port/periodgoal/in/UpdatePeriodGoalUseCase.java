package com.ddudu.application.common.port.periodgoal.in;

import com.ddudu.application.common.dto.periodgoal.request.UpdatePeriodGoalRequest;

public interface UpdatePeriodGoalUseCase {

  Long update(Long userId, Long id, UpdatePeriodGoalRequest request);

}
