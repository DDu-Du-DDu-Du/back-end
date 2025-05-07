package com.ddudu.application.port.goal.in;

import com.ddudu.application.dto.goal.request.UpdateGoalRequest;
import com.ddudu.application.dto.goal.response.GoalIdResponse;

public interface UpdateGoalUseCase {

  GoalIdResponse update(Long userId, Long id, UpdateGoalRequest request);

}
