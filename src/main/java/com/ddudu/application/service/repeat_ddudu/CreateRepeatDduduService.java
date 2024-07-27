package com.ddudu.application.service.repeat_ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.repeat_ddudu.exception.RepeatDduduErrorCode;
import com.ddudu.application.domain.repeat_ddudu.service.RepeatDduduDomainService;
import com.ddudu.application.dto.repeat_ddudu.request.CreateRepeatDduduRequest;
import com.ddudu.application.port.in.repeat_ddudu.CreateRepeatDduduUseCase;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.repeat_ddudu.SaveRepeatDduduPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateRepeatDduduService implements CreateRepeatDduduUseCase {

  private final RepeatDduduDomainService repeatDduduDomainService;
  private final SaveRepeatDduduPort saveRepeatDduduPort;
  private final GoalLoaderPort goalLoaderPort;
  private final SaveDduduPort saveDduduPort;

  @Override
  public Long create(Long loginId, CreateRepeatDduduRequest request) {
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        request.goalId(), RepeatDduduErrorCode.INVALID_GOAL.getCodeName());

    goal.validateGoalCreator(loginId);

    RepeatDdudu repeatDdudu = saveRepeatDduduPort.save(
        repeatDduduDomainService.create(request));
    saveDduduPort.saveAll(
        repeatDduduDomainService.createRepeatedDdudus(loginId, repeatDdudu));

    return repeatDdudu.getId();
  }

}
