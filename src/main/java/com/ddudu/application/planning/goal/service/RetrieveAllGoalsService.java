package com.ddudu.application.planning.goal.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.goal.exception.GoalErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.planning.goal.dto.response.BasicGoalResponse;
import com.ddudu.application.planning.goal.port.in.RetrieveAllGoalsUseCase;
import com.ddudu.application.planning.goal.port.out.GoalLoaderPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveAllGoalsService implements RetrieveAllGoalsUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;

  @Override
  public List<BasicGoalResponse> findAllByUser(Long userId) {
    User user = userLoaderPort.getUserOrElseThrow(
        userId, GoalErrorCode.USER_NOT_EXISTING.getCodeName());

    return goalLoaderPort.findAllByUserAndPrivacyTypes(user)
        .stream()
        .map(BasicGoalResponse::from)
        .toList();
  }

}
