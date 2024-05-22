package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.dto.response.GoalSummaryResponse;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.goal.RetrieveAllGoalsUseCase;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveAllGoalsService implements RetrieveAllGoalsUseCase {

  private final BaseGoalService baseGoalService;
  private final GoalLoaderPort goalLoaderPort;

  @Override
  public List<GoalSummaryResponse> findAllByUser(Long userId) {
    User user = baseGoalService.findUser(userId);

    return goalLoaderPort.findAllByUser(user)
        .stream()
        .map(GoalSummaryResponse::from)
        .toList();
  }

}
