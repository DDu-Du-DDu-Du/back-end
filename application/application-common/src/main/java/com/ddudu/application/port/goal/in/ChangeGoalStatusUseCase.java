package com.ddudu.application.port.goal.in;

import com.ddudu.application.dto.goal.request.ChangeGoalStatusRequest;
import com.ddudu.application.dto.goal.response.GoalIdResponse;

public interface ChangeGoalStatusUseCase {

  GoalIdResponse changeStatus(Long userId, Long id, ChangeGoalStatusRequest request);

}
