package com.ddudu.application.service.repeat_ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.repeat_ddudu.exception.RepeatDduduErrorCode;
import com.ddudu.application.domain.repeat_ddudu.service.RepeatDduduDomainService;
import com.ddudu.application.dto.repeat_ddudu.request.UpdateRepeatDduduRequest;
import com.ddudu.application.port.in.repeat_ddudu.UpdateRepeatDduduUseCase;
import com.ddudu.application.port.out.ddudu.DeleteDduduPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.repeat_ddudu.RepeatDduduLoaderPort;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateRepeatDduduService implements UpdateRepeatDduduUseCase {

  private final RepeatDduduDomainService repeatDduduDomainService;
  private final RepeatDduduLoaderPort repeatDduduLoaderPort;
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

    repeatDduduDomainService.update(repeatDdudu, request);

    deleteDduduPort.deleteAllByRepeatDdudu(repeatDdudu);
    saveDduduPort.saveAll(
        repeatDduduDomainService.createRepeatedDdudusAfter(
            loginId, repeatDdudu, LocalDateTime.now())
    );

    return repeatDdudu.getId();
  }

}
