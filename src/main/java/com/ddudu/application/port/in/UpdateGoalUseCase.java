package com.ddudu.application.port.in;

import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;

public interface UpdateGoalUseCase {

  GoalIdResponse update(Long userId, Long id, UpdateGoalRequest request);

}
