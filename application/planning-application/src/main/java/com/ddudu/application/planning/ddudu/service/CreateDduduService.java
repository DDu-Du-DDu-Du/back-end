package com.ddudu.application.planning.ddudu.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.service.DduduDomainService;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.port.ddudu.in.CreateDduduUseCase;
import com.ddudu.application.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.port.goal.out.GoalLoaderPort;
import com.ddudu.application.port.user.out.UserLoaderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateDduduService implements CreateDduduUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final SaveDduduPort saveDduduPort;
  private final DduduDomainService dduduDomainService;

  @Override
  public BasicDduduResponse create(Long loginId, CreateDduduRequest request) {
    // 1. 요청 유저, 목표 조회
    User user = userLoaderPort.getUserOrElseThrow(
        loginId, DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        request.goalId(), DduduErrorCode.GOAL_NOT_EXISTING.getCodeName());

    // 2. 목표 소유자의 요청인지 확인
    goal.validateGoalCreator(loginId);

    // 3. 종료되지 않은 목표인지 확인
    validateGoalNotDone(goal);

    // 4. 뚜두 생성 후 저장
    Ddudu ddudu = dduduDomainService.create(user.getId(), request.toCommand());
    return BasicDduduResponse.from(saveDduduPort.save(ddudu));
  }

  private void validateGoalNotDone(Goal goal) {
    if (goal.isDone()) {
      throw new IllegalArgumentException(DduduErrorCode.GOAL_ALREADY_DONE.getCodeName());
    }
  }

}
