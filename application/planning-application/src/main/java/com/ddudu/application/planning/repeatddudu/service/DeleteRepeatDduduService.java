package com.ddudu.application.planning.repeatddudu.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.exception.RepeatDduduErrorCode;
import com.ddudu.application.planning.repeatddudu.port.in.DeleteRepeatDduduUseCase;
import com.ddudu.application.planning.goal.port.out.GoalLoaderPort;
import com.ddudu.application.planning.repeatddudu.port.out.DeleteRepeatDduduPort;
import com.ddudu.application.planning.repeatddudu.port.out.RepeatDduduLoaderPort;
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
