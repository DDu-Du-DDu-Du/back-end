package com.ddudu.application.port.in.goal;

import com.ddudu.application.domain.goal.dto.response.BasicGoalWithStatusResponse;
import java.util.List;

public interface RetrieveAllGoalsUseCase {

  List<BasicGoalWithStatusResponse> findAllByUser(Long userId);

}
