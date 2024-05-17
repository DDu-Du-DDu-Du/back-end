package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.port.in.goal.UpdateGoalUseCase;
import com.ddudu.application.port.out.goal.UpdateGoalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateGoalService implements UpdateGoalUseCase {

  private final BaseGoalService baseGoalService;
  private final UpdateGoalPort updateGoalPort;

  @Override
  public GoalIdResponse update(Long userId, Long id, UpdateGoalRequest request) {
    Goal goal = baseGoalService.findGoal(id);

    baseGoalService.checkGoalOwnership(userId, goal);

    Goal updated = goal.applyGoalUpdates(
        request.name(),
        request.color(),
        PrivacyType.from(request.privacyType())
    );

    return GoalIdResponse.from(updateGoalPort.update(updated));
  }

}
