package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.goal.service.GoalDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.goal.CreateGoalUseCase;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import jakarta.transaction.Transactional;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateGoalService implements CreateGoalUseCase {

  private final GoalDomainService goalDomainService;
  private final UserLoaderPort userLoaderPort;
  private final SaveGoalPort saveGoalPort;

  @Override
  public GoalIdResponse create(Long userId, CreateGoalRequest request) {
    User user = findUser(userId);
    Goal goal = goalDomainService.create(user, request);

    return GoalIdResponse.from(saveGoalPort.save(goal));
  }

  private User findUser(Long userId) {
    return userLoaderPort.findById(userId)
        .orElseThrow(
            () -> new MissingResourceException(
                GoalErrorCode.USER_NOT_EXISTING.getCodeName(),
                User.class.getName(),
                userId.toString()
            ));
  }

}
