package com.ddudu.application.port.in.goal;

import com.ddudu.application.dto.goal.response.BasicGoalResponse;
import java.util.List;

public interface RetrieveAllGoalsUseCase {

  List<BasicGoalResponse> findAllByUser(Long userId);

}
