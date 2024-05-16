package com.ddudu.application.port.in;

import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;

public interface UpdateGoalUseCase {

  GoalResponse update(Long userId, Long id, UpdateGoalRequest request);

}
