package com.ddudu.application.port.in.goal;

import com.ddudu.application.dto.goal.request.CreateGoalRequest;
import com.ddudu.application.dto.goal.response.GoalIdResponse;

public interface CreateGoalUseCase {

  GoalIdResponse create(Long userId, CreateGoalRequest request);

}
