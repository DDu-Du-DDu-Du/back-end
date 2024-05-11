package com.ddudu.application.port.in;

import com.ddudu.application.domain.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.CreateGoalResponse;

public interface CreateGoalUseCase {

  CreateGoalResponse create(Long userId, CreateGoalRequest request);

}
