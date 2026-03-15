package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.UpdateDduduRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.common.dto.interim.InterimCancelReminderEvent;
import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.port.ddudu.in.UpdateDduduUseCase;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.DduduUpdatePort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.service.DduduDomainService;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateDduduService implements UpdateDduduUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final DduduLoaderPort dduduLoaderPort;
  private final DduduUpdatePort dduduUpdatePort;
  private final DduduDomainService dduduDomainService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  public BasicDduduResponse update(Long loginId, Long dduduId, UpdateDduduRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        dduduId,
        DduduErrorCode.ID_NOT_EXISTING.getCodeName()
    );
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        request.goalId(),
        DduduErrorCode.GOAL_NOT_EXISTING.getCodeName()
    );

    ddudu.validateDduduCreator(user.getId());
    goal.validateGoalCreator(user.getId());

    Ddudu updatedDdudu = dduduDomainService.update(ddudu, request.toCommand());
    Ddudu saved = dduduUpdatePort.update(updatedDdudu);

    if (ddudu.hasReminder()) {
      InterimCancelReminderEvent cancelEvent = InterimCancelReminderEvent.from(user.getId(), ddudu);
      applicationEventPublisher.publishEvent(cancelEvent);
    }

    if (saved.hasReminder()) {
      InterimSetReminderEvent setEvent = InterimSetReminderEvent.from(user.getId(), saved);
      applicationEventPublisher.publishEvent(setEvent);
    }

    return BasicDduduResponse.from(saved);
  }

}
