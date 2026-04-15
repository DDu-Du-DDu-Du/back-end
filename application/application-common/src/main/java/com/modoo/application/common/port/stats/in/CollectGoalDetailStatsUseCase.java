package com.modoo.application.common.port.stats.in;

import com.modoo.application.common.dto.stats.response.GoalDetailStatsSummaryResponse;

public interface CollectGoalDetailStatsUseCase {

  GoalDetailStatsSummaryResponse collectDetail(Long loginId, Long goalId, Long userId);

}
