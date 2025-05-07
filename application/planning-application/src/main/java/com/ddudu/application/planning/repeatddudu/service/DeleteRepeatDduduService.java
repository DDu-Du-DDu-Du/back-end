package com.ddudu.application.planning.repeatddudu.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.common.exception.RepeatDduduErrorCode;
import com.ddudu.application.port.repeatddudu.in.DeleteRepeatDduduUseCase;
import com.ddudu.application.port.goal.out.GoalLoaderPort;
import com.ddudu.application.port.repeatddudu.out.DeleteRepeatDduduPort;
import com.ddudu.application.port.repeatddudu.out.RepeatDduduLoaderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class DeleteRepeatDduduService implements DeleteRepeatDduduUseCase {

  private final RepeatDduduLoaderPort repeatDduduLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final DeleteRepeatDduduPort deleteRepeatDduduPort;

  public void delete(Long userId, Long id) {
    /**
     * 하위 뚜두들도 모두 삭제
     */
    repeatDduduLoaderPort.getOptionalRepeatDdudu(id)
        .ifPresent(repeatDdudu -> {
          Goal goal = goalLoaderPort.getGoalOrElseThrow(
              repeatDdudu.getGoalId(),
              RepeatDduduErrorCode.INVALID_GOAL.getCodeName()
          );
          goal.validateGoalCreator(userId);
          deleteRepeatDduduPort.deleteWithDdudus(repeatDdudu);
        });
  }

}
