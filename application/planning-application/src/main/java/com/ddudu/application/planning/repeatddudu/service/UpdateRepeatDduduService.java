package com.ddudu.application.planning.repeatddudu.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.common.exception.RepeatDduduErrorCode;
import com.ddudu.domain.planning.repeatddudu.service.RepeatDduduDomainService;
import com.ddudu.application.dto.repeatddudu.request.UpdateRepeatDduduRequest;
import com.ddudu.application.port.repeatddudu.in.UpdateRepeatDduduUseCase;
import com.ddudu.application.port.ddudu.out.DeleteDduduPort;
import com.ddudu.application.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.port.goal.out.GoalLoaderPort;
import com.ddudu.application.port.repeatddudu.out.RepeatDduduLoaderPort;
import com.ddudu.application.port.repeatddudu.out.UpdateRepeatDduduPort;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateRepeatDduduService implements UpdateRepeatDduduUseCase {

  private final RepeatDduduDomainService repeatDduduDomainService;
  private final RepeatDduduLoaderPort repeatDduduLoaderPort;
  private final UpdateRepeatDduduPort updateRepeatDduduPort;
  private final DeleteDduduPort deleteDduduPort;
  private final GoalLoaderPort goalLoaderPort;
  private final SaveDduduPort saveDduduPort;

  @Override
  public Long update(Long loginId, Long id, UpdateRepeatDduduRequest request) {
    RepeatDdudu repeatDdudu = repeatDduduLoaderPort.getOrElseThrow(
        id, RepeatDduduErrorCode.REPEAT_DDUDU_NOT_EXIST.getCodeName());
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        repeatDdudu.getGoalId(), RepeatDduduErrorCode.INVALID_GOAL.getCodeName());

    goal.validateGoalCreator(loginId);

    repeatDdudu = updateRepeatDduduPort.update(
        repeatDduduDomainService.update(repeatDdudu, request.toCommand()));

    deleteDduduPort.deleteAllByRepeatDdudu(repeatDdudu);
    saveDduduPort.saveAll(
        repeatDduduDomainService.createRepeatedDdudusAfter(
            loginId, repeatDdudu, LocalDateTime.now())
    );

    return repeatDdudu.getId();
  }

}
