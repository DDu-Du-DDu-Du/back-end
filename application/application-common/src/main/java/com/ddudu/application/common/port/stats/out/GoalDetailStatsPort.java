package com.ddudu.application.common.port.stats.out;

import com.ddudu.application.common.dto.stats.GoalStatusSummaryRaw;
import java.util.List;

public interface GoalDetailStatsPort {

  List<GoalStatusSummaryRaw> loadGoalStatuses(Long userId, Long goalId);

}
