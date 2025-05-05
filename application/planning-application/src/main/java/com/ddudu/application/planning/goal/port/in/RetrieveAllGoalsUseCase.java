package com.ddudu.application.planning.goal.port.in;

import com.ddudu.application.planning.goal.dto.response.BasicGoalResponse;
import java.util.List;

public interface RetrieveAllGoalsUseCase {

  List<BasicGoalResponse> findAllByUser(Long userId);

}
