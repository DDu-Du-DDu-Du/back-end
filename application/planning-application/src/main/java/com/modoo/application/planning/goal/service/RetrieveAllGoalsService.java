package com.modoo.application.planning.goal.service;

import com.modoo.application.common.dto.goal.response.BasicGoalResponse;
import com.modoo.application.common.port.goal.in.RetrieveAllGoalsUseCase;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.GoalErrorCode;
import com.modoo.domain.user.user.aggregate.User;
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
        userId,
        GoalErrorCode.USER_NOT_EXISTING.getCodeName()
    );

    return goalLoaderPort.findAllByUserAndPrivacyTypes(user.getId())
        .stream()
        .map(BasicGoalResponse::from)
        .toList();
  }

}
