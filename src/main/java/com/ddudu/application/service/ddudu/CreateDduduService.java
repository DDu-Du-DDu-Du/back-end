package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.ddudu.service.DduduDomainService;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.port.in.ddudu.CreateDduduUseCase;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
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

    // 2. 생성 권한 검증
    goal.validateGoalCreator(loginId);

    // TODO: 완료된 목표에 대한 생성은 거절하도록 변경

    Ddudu ddudu = dduduDomainService.create(user, request);

    return BasicDduduResponse.from(saveDduduPort.save(ddudu));
  }

}
