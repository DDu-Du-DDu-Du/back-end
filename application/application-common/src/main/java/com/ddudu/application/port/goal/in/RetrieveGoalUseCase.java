package com.ddudu.application.port.goal.in;

import com.ddudu.application.dto.goal.response.GoalWithRepeatDduduResponse;

public interface RetrieveGoalUseCase {

  GoalWithRepeatDduduResponse getById(Long userId, Long id);

}
