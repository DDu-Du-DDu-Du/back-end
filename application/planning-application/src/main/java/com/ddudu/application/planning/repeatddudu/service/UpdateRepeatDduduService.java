package com.ddudu.application.planning.repeatddudu.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.planning.repeatddudu.exception.RepeatDduduErrorCode;
import com.ddudu.domain.planning.repeatddudu.service.RepeatDduduDomainService;
import com.ddudu.application.planning.repeatddudu.dto.request.UpdateRepeatDduduRequest;
import com.ddudu.application.planning.repeatddudu.port.in.UpdateRepeatDduduUseCase;
import com.ddudu.application.planning.ddudu.port.out.DeleteDduduPort;
import com.ddudu.application.planning.ddudu.port.out.SaveDduduPort;
import com.ddudu.application.planning.goal.port.out.GoalLoaderPort;
import com.ddudu.application.planning.repeatddudu.port.out.RepeatDduduLoaderPort;
import com.ddudu.application.planning.repeatddudu.port.out.UpdateRepeatDduduPort;
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
        repeatDduduDomainService.update(repeatDdudu, request));

    deleteDduduPort.deleteAllByRepeatDdudu(repeatDdudu);
    saveDduduPort.saveAll(
        repeatDduduDomainService.createRepeatedDdudusAfter(
            loginId, repeatDdudu, LocalDateTime.now())
    );

    return repeatDdudu.getId();
  }

}
