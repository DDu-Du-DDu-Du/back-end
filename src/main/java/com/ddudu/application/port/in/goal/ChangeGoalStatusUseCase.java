package com.ddudu.application.port.in.goal;

import com.ddudu.application.domain.goal.dto.request.ChangeGoalStatusRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;

public interface ChangeGoalStatusUseCase {

  GoalIdResponse changeStatus(Long userId, Long id, ChangeGoalStatusRequest request);

}
