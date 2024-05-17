package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.dto.request.ChangeGoalStatusRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.port.in.goal.ChangeGoalStatusUseCase;
import com.ddudu.application.port.out.goal.UpdateGoalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ChangeGoalStatusService implements ChangeGoalStatusUseCase {

  private final BaseGoalService baseGoalService;
  private final UpdateGoalPort updateGoalPort;

  @Override
  public GoalIdResponse changeStatus(Long userId, Long id, ChangeGoalStatusRequest request) {
    Goal goal = baseGoalService.findGoal(id);

    baseGoalService.checkGoalOwnership(userId, goal);

    Goal updated = goal.changeStatus(GoalStatus.from(request.status()));

    return GoalIdResponse.from(updateGoalPort.update(updated));
  }

}
