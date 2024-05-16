package com.ddudu.application.port.in.goal;

import com.ddudu.application.domain.goal.dto.response.GoalSummaryResponse;
import java.util.List;

public interface RetrieveAllGoalsUseCase {

  List<GoalSummaryResponse> findAllByUser(Long userId);

}
