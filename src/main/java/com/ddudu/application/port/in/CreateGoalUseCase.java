package com.ddudu.application.port.in;

import com.ddudu.application.domain.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;

public interface CreateGoalUseCase {

  GoalIdResponse create(Long userId, CreateGoalRequest request);

}
