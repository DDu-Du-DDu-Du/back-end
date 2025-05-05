package com.ddudu.application.planning.goal.port.in;

import com.ddudu.application.planning.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.planning.goal.dto.response.GoalIdResponse;

public interface CreateGoalUseCase {

  GoalIdResponse create(Long userId, CreateGoalRequest request);

}
