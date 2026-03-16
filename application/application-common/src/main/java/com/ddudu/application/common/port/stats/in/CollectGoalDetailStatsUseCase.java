package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.response.GoalDetailStatsSummaryResponse;

public interface CollectGoalDetailStatsUseCase {

  GoalDetailStatsSummaryResponse collectDetail(Long loginId, Long goalId, Long userId);

}
