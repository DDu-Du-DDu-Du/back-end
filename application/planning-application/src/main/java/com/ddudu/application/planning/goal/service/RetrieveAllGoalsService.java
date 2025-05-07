package com.ddudu.application.planning.goal.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.dto.goal.response.BasicGoalResponse;
import com.ddudu.application.port.goal.in.RetrieveAllGoalsUseCase;
import com.ddudu.application.port.goal.out.GoalLoaderPort;
import com.ddudu.application.port.user.out.UserLoaderPort;
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

    return goalLoaderPort.findAllByUserAndPrivacyTypes(user.getId())
        .stream()
        .map(BasicGoalResponse::from)
        .toList();
  }

}
