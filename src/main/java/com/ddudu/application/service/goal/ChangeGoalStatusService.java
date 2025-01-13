package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.dto.goal.request.ChangeGoalStatusRequest;
import com.ddudu.application.dto.goal.response.GoalIdResponse;
import com.ddudu.application.port.in.goal.ChangeGoalStatusUseCase;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.goal.UpdateGoalPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ChangeGoalStatusService implements ChangeGoalStatusUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final UpdateGoalPort updateGoalPort;

  @Override
  public GoalIdResponse changeStatus(Long userId, Long id, ChangeGoalStatusRequest request) {
    // 1. 목표 조회 및 검증
    Goal goal = goalLoaderPort.getGoalOrElseThrow(id, GoalErrorCode.ID_NOT_EXISTING.getCodeName());

    goal.validateGoalCreator(userId);

    // 2. 목표 상태 변경
    Goal updated = goal.changeStatus(GoalStatus.from(request.status()));

    return GoalIdResponse.from(updateGoalPort.update(updated));
  }

}
