package com.modoo.application.common.port.goal.in;

import com.modoo.application.common.dto.goal.response.BasicGoalResponse;
import java.util.List;

public interface RetrieveAllGoalsUseCase {

  List<BasicGoalResponse> findAllByUser(Long userId);

}
