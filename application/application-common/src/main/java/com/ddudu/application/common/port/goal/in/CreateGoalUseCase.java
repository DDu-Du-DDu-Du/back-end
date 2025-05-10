package com.ddudu.application.common.port.goal.in;

import com.ddudu.application.common.dto.goal.request.CreateGoalRequest;
import com.ddudu.application.common.dto.goal.response.GoalIdResponse;

public interface CreateGoalUseCase {

  GoalIdResponse create(Long userId, CreateGoalRequest request);

}
