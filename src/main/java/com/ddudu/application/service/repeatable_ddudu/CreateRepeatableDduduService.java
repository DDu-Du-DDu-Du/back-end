package com.ddudu.application.service.repeatable_ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeatable_ddudu.domain.RepeatableDdudu;
import com.ddudu.application.domain.repeatable_ddudu.service.RepeatableDduduDomainService;
import com.ddudu.application.dto.repeatable_ddudu.requset.CreateRepeatableDduduRequest;
import com.ddudu.application.port.in.repeatable_ddudu.CreateRepeatableDduduUseCase;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.repeatable_ddudu.SaveRepeatableDduduPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateRepeatableDduduService implements CreateRepeatableDduduUseCase {

  private final RepeatableDduduDomainService repeatableDduduDomainService;
  private final SaveRepeatableDduduPort saveRepeatableDduduPort;
  private final GoalLoaderPort goalLoaderPort;
  private final SaveDduduPort saveDduduPort;

  @Override
  public Long create(Long loginId, CreateRepeatableDduduRequest request) {
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        request.goalId(), DduduErrorCode.GOAL_NOT_EXISTING.getCodeName());

    goal.validateGoalCreator(loginId);

    RepeatableDdudu repeatableDdudu = saveRepeatableDduduPort.save(
        repeatableDduduDomainService.create(request));
    saveDduduPort.saveAll(
        repeatableDduduDomainService.createRepeatedDdudus(loginId, repeatableDdudu));

    return repeatableDdudu.getId();
  }

}
