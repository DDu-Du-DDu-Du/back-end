package com.modoo.application.common.port.goal.in;

import com.modoo.application.common.dto.goal.request.CreateGoalRequest;
import com.modoo.application.common.dto.goal.response.GoalIdResponse;

public interface CreateGoalUseCase {

  GoalIdResponse create(Long userId, CreateGoalRequest request);

}
