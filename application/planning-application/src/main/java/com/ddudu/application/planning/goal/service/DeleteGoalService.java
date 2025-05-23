package com.ddudu.application.planning.goal.service;

import com.ddudu.application.common.port.goal.in.DeleteGoalUseCase;
import com.ddudu.application.common.port.goal.out.DeleteGoalPort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.repeatddudu.out.DeleteRepeatDduduPort;
import com.ddudu.common.annotation.UseCase;
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
