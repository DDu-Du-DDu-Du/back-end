package com.modoo.application.common.port.stats.out;

import com.modoo.application.common.dto.stats.GoalStatusSummaryRaw;
import java.util.List;

public interface GoalDetailStatsPort {

  List<GoalStatusSummaryRaw> loadGoalStatuses(Long userId, Long goalId);

}
