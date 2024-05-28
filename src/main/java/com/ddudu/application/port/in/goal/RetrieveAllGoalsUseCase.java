package com.ddudu.application.port.in.goal;

import com.ddudu.application.dto.goal.response.BasicGoalWithStatusResponse;
import java.util.List;

public interface RetrieveAllGoalsUseCase {

  List<BasicGoalWithStatusResponse> findAllByUser(Long userId);

}
