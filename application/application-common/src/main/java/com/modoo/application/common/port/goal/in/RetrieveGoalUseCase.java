package com.modoo.application.common.port.goal.in;

import com.modoo.application.common.dto.goal.response.GoalWithRepeatTodoResponse;

public interface RetrieveGoalUseCase {

  GoalWithRepeatTodoResponse getById(Long userId, Long id);

}
