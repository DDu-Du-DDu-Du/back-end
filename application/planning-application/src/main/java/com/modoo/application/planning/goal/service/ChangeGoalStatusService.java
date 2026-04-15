package com.modoo.application.planning.goal.service;

import com.modoo.application.common.dto.goal.request.ChangeGoalStatusRequest;
import com.modoo.application.common.dto.goal.response.GoalIdResponse;
import com.modoo.application.common.port.goal.in.ChangeGoalStatusUseCase;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.goal.out.UpdateGoalPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.GoalErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.goal.aggregate.enums.GoalStatus;
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
