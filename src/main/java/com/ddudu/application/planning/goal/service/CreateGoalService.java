package com.ddudu.application.planning.goal.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.exception.GoalErrorCode;
import com.ddudu.domain.planning.goal.service.GoalDomainService;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.planning.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.planning.goal.dto.request.CreateRepeatDduduRequestWithoutGoal;
import com.ddudu.application.planning.goal.dto.response.GoalIdResponse;
import com.ddudu.application.planning.repeatddudu.dto.request.CreateRepeatDduduRequest;
import com.ddudu.application.planning.goal.port.in.CreateGoalUseCase;
import com.ddudu.application.planning.goal.port.out.SaveGoalPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
import com.ddudu.application.planning.repeatddudu.service.CreateRepeatDduduService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateGoalService implements CreateGoalUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalDomainService goalDomainService;
  private final CreateRepeatDduduService createRepeatDduduService;
  private final SaveGoalPort saveGoalPort;

  @Override
  public GoalIdResponse create(Long userId, CreateGoalRequest request) {
    // 1. 사용자 조회 및 검증
    User user = userLoaderPort.getUserOrElseThrow(
        userId, GoalErrorCode.USER_NOT_EXISTING.getCodeName());

    // 2. 목표 생성 후 저장
    Goal goal = saveGoalPort.save(goalDomainService.create(user, request));

    // 3. 반복 뚜두 생성 후 저장
    // TODO: 반복 뚜두 생성 부분을 비동기로 변경
    request.repeatDdudus()
        .forEach(repeatDduduRequest ->
            createRepeatDduduService.create(
                userId, toCreateRepeatDduduRequest(repeatDduduRequest, goal))
        );

    return GoalIdResponse.from(goal);
  }

  private CreateRepeatDduduRequest toCreateRepeatDduduRequest(
      CreateRepeatDduduRequestWithoutGoal repeatDduduRequest, Goal goal
  ) {
    return new CreateRepeatDduduRequest(
        repeatDduduRequest.name(),
        goal.getId(),
        repeatDduduRequest.repeatType(),
        repeatDduduRequest.repeatDaysOfWeek(),
        repeatDduduRequest.repeatDaysOfMonth(),
        repeatDduduRequest.lastDayOfMonth(),
        repeatDduduRequest.startDate(),
        repeatDduduRequest.endDate(),
        repeatDduduRequest.beginAt(),
        repeatDduduRequest.endAt()
    );
  }

}
