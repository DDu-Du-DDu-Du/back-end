package com.ddudu.application.common.port.goal.in;

import com.ddudu.application.common.dto.goal.request.UpdateGoalRequest;
import com.ddudu.application.common.dto.goal.response.GoalIdResponse;

public interface UpdateGoalUseCase {

  GoalIdResponse update(Long userId, Long id, UpdateGoalRequest request);

}
