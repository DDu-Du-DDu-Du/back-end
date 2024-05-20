package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.port.in.goal.DeleteGoalUseCase;
import com.ddudu.application.port.out.goal.DeleteGoalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class DeleteGoalService implements DeleteGoalUseCase {

  private final BaseGoalService baseGoalService;
  private final DeleteGoalPort deleteGoalPort;

  @Override
  public void delete(Long userId, Long id) {
    Goal goal = baseGoalService.findGoal(id);

    goal.validateGoalCreator(userId);
    deleteGoalPort.deleteWithDdudus(goal);
  }

}
