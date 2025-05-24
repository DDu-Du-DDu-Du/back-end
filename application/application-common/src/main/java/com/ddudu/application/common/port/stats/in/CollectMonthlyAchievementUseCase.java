package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.AchievementPerGoal;
import com.ddudu.application.common.dto.stats.response.GenericStatsResponse;
import java.time.YearMonth;

public interface CollectMonthlyAchievementUseCase {

  GenericStatsResponse<AchievementPerGoal> collectAchievement(Long loginId, YearMonth yearMonth);

}
