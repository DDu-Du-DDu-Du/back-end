package com.modoo.application.planning.goal.service;

import com.modoo.application.common.dto.goal.request.CreateGoalRequest;
import com.modoo.application.common.dto.goal.request.CreateRepeatTodoRequestWithoutGoal;
import com.modoo.application.common.dto.goal.response.GoalIdResponse;
import com.modoo.application.common.dto.repeattodo.request.CreateRepeatTodoRequest;
import com.modoo.application.common.port.goal.in.CreateGoalUseCase;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.application.planning.repeattodo.service.CreateRepeatTodoService;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.GoalErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.goal.dto.CreateGoalCommand;
import com.modoo.domain.planning.goal.service.GoalDomainService;
import com.modoo.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateGoalService implements CreateGoalUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final GoalDomainService goalDomainService;
  private final CreateRepeatTodoService createRepeatTodoService;
  private final SaveGoalPort saveGoalPort;

  @Override
  public GoalIdResponse create(Long userId, CreateGoalRequest request) {
    // 1. 사용자 조회 및 검증
    User user = userLoaderPort.getUserOrElseThrow(
        userId,
        GoalErrorCode.USER_NOT_EXISTING.getCodeName()
    );

    // 2. 목표 생성 후 저장
    int nextPriority = goalLoaderPort.findMaxPriorityByUserId(user.getId()) + 1;
    CreateGoalCommand command = CreateGoalCommand.builder()
        .name(request.name())
        .color(request.color())
        .privacyType(request.privacyType())
        .priority(nextPriority)
        .build();

    Goal goal = saveGoalPort.save(goalDomainService.create(user.getId(), command));

    // 3. 반복 투두 생성 후 저장
    // TODO: 반복 투두 생성 부분을 비동기로 변경
    request.repeatTodos()
        .forEach(repeatTodoRequest -> createRepeatTodoService.create(
            userId,
            toCreateRepeatTodoRequest(repeatTodoRequest, goal)
        ));

    return GoalIdResponse.from(goal);
  }

  private CreateRepeatTodoRequest toCreateRepeatTodoRequest(
      CreateRepeatTodoRequestWithoutGoal repeatTodoRequest,
      Goal goal
  ) {
    return new CreateRepeatTodoRequest(
        repeatTodoRequest.name(),
        goal.getId(),
        repeatTodoRequest.repeatType(),
        repeatTodoRequest.repeatDaysOfWeek(),
        repeatTodoRequest.repeatDaysOfMonth(),
        repeatTodoRequest.lastDayOfMonth(),
        repeatTodoRequest.startDate(),
        repeatTodoRequest.endDate(),
        repeatTodoRequest.beginAt(),
        repeatTodoRequest.endAt()
    );
  }

}
