package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.port.in.goal.DeleteGoalUseCase;
import com.ddudu.application.port.out.goal.DeleteGoalPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.repeat_ddudu.DeleteRepeatDduduPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class DeleteGoalService implements DeleteGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final DeleteGoalPort deleteGoalPort;
  private final DeleteRepeatDduduPort deleteRepeatDduduPort;

  @Override
  public void delete(Long userId, Long goalId) {
    goalLoaderPort.getOptionalGoal(goalId)
        .ifPresent(goal -> {
          goal.validateGoalCreator(userId);
          deleteRepeatDduduPort.deleteAllWithDdudusByGoal(goal);
          deleteGoalPort.deleteWithDdudus(goal);
        });
  }

}
