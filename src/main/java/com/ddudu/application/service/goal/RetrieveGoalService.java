package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.dto.goal.response.GoalWithRepeatDduduResponse;
import com.ddudu.application.port.in.goal.RetrieveGoalUseCase;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.repeat_ddudu.RepeatDduduLoaderPort;
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
