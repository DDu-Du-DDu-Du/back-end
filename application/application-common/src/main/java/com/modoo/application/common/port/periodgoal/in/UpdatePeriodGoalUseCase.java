package com.modoo.application.common.port.periodgoal.in;

import com.modoo.application.common.dto.periodgoal.request.UpdatePeriodGoalRequest;

public interface UpdatePeriodGoalUseCase {

  Long update(Long userId, Long id, UpdatePeriodGoalRequest request);

}
