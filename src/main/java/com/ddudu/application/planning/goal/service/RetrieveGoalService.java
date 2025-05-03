package com.ddudu.application.planning.goal.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.exception.GoalErrorCode;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.application.planning.goal.dto.response.GoalWithRepeatDduduResponse;
import com.ddudu.application.planning.goal.port.in.RetrieveGoalUseCase;
import com.ddudu.application.planning.goal.port.out.GoalLoaderPort;
import com.ddudu.application.planning.repeatddudu.port.out.RepeatDduduLoaderPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveGoalService implements RetrieveGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final RepeatDduduLoaderPort repeatDduduLoaderPort;

  @Override
  public GoalWithRepeatDduduResponse getById(Long userId, Long id) {
    Goal goal = goalLoaderPort.getGoalOrElseThrow(id, GoalErrorCode.ID_NOT_EXISTING.getCodeName());

    goal.validateGoalCreator(userId);

    List<RepeatDdudu> repeatDdudus = repeatDduduLoaderPort.getAllByGoal(goal);

    return GoalWithRepeatDduduResponse.from(goal, repeatDdudus);
  }

}
