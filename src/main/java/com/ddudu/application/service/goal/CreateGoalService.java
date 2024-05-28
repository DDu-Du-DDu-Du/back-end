package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.dto.goal.request.CreateGoalRequest;
import com.ddudu.application.dto.goal.response.GoalIdResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.goal.service.GoalDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.goal.CreateGoalUseCase;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateGoalService implements CreateGoalUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalDomainService goalDomainService;
  private final SaveGoalPort saveGoalPort;

  @Override
  public GoalIdResponse create(Long userId, CreateGoalRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        userId, GoalErrorCode.USER_NOT_EXISTING.getCodeName());
    Goal goal = goalDomainService.create(user, request);

    return GoalIdResponse.from(saveGoalPort.save(goal));
  }

}
