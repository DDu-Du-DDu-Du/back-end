package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;
import com.ddudu.application.port.in.goal.RetrieveGoalUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveGoalService implements RetrieveGoalUseCase {

  private final BaseGoalService baseGoalService;

  @Override
  public GoalResponse getById(Long userId, Long id) {
    Goal goal = baseGoalService.findGoal(id);

    goal.validateGoalCreator(userId);
    return GoalResponse.from(goal);
  }

}
