package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.dto.response.GoalResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.port.in.goal.RetrieveGoalUseCase;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveGoalService implements RetrieveGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;

  @Override
  public GoalResponse getById(Long userId, Long id) {
    Goal goal = goalLoaderPort.getGoalOrElseThrow(id, GoalErrorCode.ID_NOT_EXISTING.getCodeName());

    goal.validateGoalCreator(userId);
    return GoalResponse.from(goal);
  }

}
