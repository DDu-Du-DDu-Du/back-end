package com.ddudu.application.port.in;

import com.ddudu.application.domain.goal.dto.request.ChangeGoalStatusRequest;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;

public interface ChangeGoalStatusUseCase {

  GoalResponse changeStatus(Long userId, Long id, ChangeGoalStatusRequest request);

}
