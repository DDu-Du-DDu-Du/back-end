package com.ddudu.application.common.port.goal.in;

import com.ddudu.application.common.dto.goal.request.ChangeGoalStatusRequest;
import com.ddudu.application.common.dto.goal.response.GoalIdResponse;

public interface ChangeGoalStatusUseCase {

  GoalIdResponse changeStatus(Long userId, Long id, ChangeGoalStatusRequest request);

}
