package com.ddudu.application.common.port.stats.in;

import com.ddudu.application.common.dto.stats.response.AchievedStatsDetailResponse;
import com.ddudu.application.common.dto.stats.response.PostponedStatsDetailResponse;
import java.time.YearMonth;

public interface CollectMonthlyStatsDetailUseCase {

  AchievedStatsDetailResponse collectAchievedDetail(
      Long loginId,
      Long goalId,
      YearMonth fromMonth,
      YearMonth toMonth
  );

  PostponedStatsDetailResponse collectPostponedDetail(
      Long loginId,
      Long goalId,
      YearMonth fromMonth,
      YearMonth toMonth
  );

}
