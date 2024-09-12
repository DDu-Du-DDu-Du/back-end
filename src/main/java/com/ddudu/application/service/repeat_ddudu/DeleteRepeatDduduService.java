package com.ddudu.application.service.repeat_ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.repeat_ddudu.exception.RepeatDduduErrorCode;
import com.ddudu.application.port.in.repeat_ddudu.DeleteRepeatDduduUseCase;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.repeat_ddudu.DeleteRepeatDduduPort;
import com.ddudu.application.port.out.repeat_ddudu.RepeatDduduLoaderPort;
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
          deleteRepeatDduduPort.delete(repeatDdudu);
        });
  }

}
