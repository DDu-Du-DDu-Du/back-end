package com.ddudu.application.common.port.goal.in;

import com.ddudu.application.common.dto.goal.response.BasicGoalResponse;
import java.util.List;

public interface RetrieveAllGoalsUseCase {

  List<BasicGoalResponse> findAllByUser(Long userId);

}
