package com.ddudu.application.port.in.goal;

import com.ddudu.application.domain.goal.dto.response.GoalResponse;

public interface RetrieveGoalUseCase {

  GoalResponse getById(Long userId, Long id);

}
