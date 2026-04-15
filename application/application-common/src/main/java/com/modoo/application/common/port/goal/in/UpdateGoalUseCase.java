package com.modoo.application.common.port.goal.in;

import com.modoo.application.common.dto.goal.request.UpdateGoalRequest;
import com.modoo.application.common.dto.goal.response.GoalIdResponse;

public interface UpdateGoalUseCase {

  GoalIdResponse update(Long userId, Long id, UpdateGoalRequest request);

}
