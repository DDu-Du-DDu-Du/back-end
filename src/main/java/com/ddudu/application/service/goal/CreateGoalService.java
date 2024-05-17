package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.domain.goal.service.GoalDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.goal.CreateGoalUseCase;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateGoalService implements CreateGoalUseCase {

  private final BaseGoalService baseGoalService;
  private final GoalDomainService goalDomainService;
  private final SaveGoalPort saveGoalPort;

  @Override
  public GoalIdResponse create(Long userId, CreateGoalRequest request) {
    User user = baseGoalService.findUser(userId);
    Goal goal = goalDomainService.create(user, request);

    return GoalIdResponse.from(saveGoalPort.save(goal));
  }

}
