package com.ddudu.application.common.port.goal.in;

import com.ddudu.application.common.dto.goal.response.GoalWithRepeatTodoResponse;

public interface RetrieveGoalUseCase {

  GoalWithRepeatTodoResponse getById(Long userId, Long id);

}
