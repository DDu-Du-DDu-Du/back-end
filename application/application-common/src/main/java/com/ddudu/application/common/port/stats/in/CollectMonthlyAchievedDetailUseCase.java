package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.response.AchievedStatsDetailResponse;
import java.time.YearMonth;

public interface CollectMonthlyAchievedDetailUseCase {

  AchievedStatsDetailResponse collectAchievedDetail(
      Long loginId,
      Long goalId,
      YearMonth fromMonth,
      YearMonth toMonth
  );

}
