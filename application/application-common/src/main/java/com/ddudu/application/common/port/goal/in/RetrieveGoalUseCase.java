package com.ddudu.application.common.port.goal.in;

import com.ddudu.application.common.dto.goal.response.GoalWithRepeatDduduResponse;

public interface RetrieveGoalUseCase {

  GoalWithRepeatDduduResponse getById(Long userId, Long id);

}
