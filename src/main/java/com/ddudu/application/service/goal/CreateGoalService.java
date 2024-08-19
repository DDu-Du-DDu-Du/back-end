package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.goal.service.GoalDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.goal.request.CreateGoalRequest;
import com.ddudu.application.dto.goal.request.CreateRepeatDduduRequestWithoutGoal;
import com.ddudu.application.dto.goal.response.GoalIdResponse;
import com.ddudu.application.dto.repeat_ddudu.request.CreateRepeatDduduRequest;
import com.ddudu.application.port.in.goal.CreateGoalUseCase;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import com.ddudu.application.service.repeat_ddudu.CreateRepeatDduduService;
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
    User user = userLoaderPort.getUserOrElseThrow(
        userId, GoalErrorCode.USER_NOT_EXISTING.getCodeName());
    Goal goal = saveGoalPort.save(goalDomainService.create(user, request));

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
