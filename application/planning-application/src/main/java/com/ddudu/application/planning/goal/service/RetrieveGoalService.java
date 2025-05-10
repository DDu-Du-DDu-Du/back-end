package com.ddudu.application.planning.goal.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.application.common.dto.goal.response.GoalWithRepeatDduduResponse;
import com.ddudu.application.common.port.goal.in.RetrieveGoalUseCase;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.repeatddudu.out.RepeatDduduLoaderPort;
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
