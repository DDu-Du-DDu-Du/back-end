package com.ddudu.application.planning.goal.port.in;

import com.ddudu.application.planning.goal.dto.response.GoalWithRepeatDduduResponse;

public interface RetrieveGoalUseCase {

  GoalWithRepeatDduduResponse getById(Long userId, Long id);

}
