package com.ddudu.application.planning.goal.port.in;

import com.ddudu.application.planning.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.planning.goal.dto.response.GoalIdResponse;

public interface UpdateGoalUseCase {

  GoalIdResponse update(Long userId, Long id, UpdateGoalRequest request);

}
