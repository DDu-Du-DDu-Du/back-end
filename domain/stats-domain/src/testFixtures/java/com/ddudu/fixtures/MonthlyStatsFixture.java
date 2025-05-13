package com.ddudu.fixtures;

import com.ddudu.aggregate.BaseStats;
import com.ddudu.aggregate.MonthlyStats;
import com.ddudu.fixture.BaseFixture;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MonthlyStatsFixture extends BaseFixture {

  public static MonthlyStats createEmptyStats(Long userId, YearMonth yearMonth) {
    return MonthlyStatsFixture.createMonthlyStats(userId, yearMonth, Collections.emptyList());
  }

  public static MonthlyStats createMonthlyStats(
      Long userId,
      YearMonth yearMonth,
      List<BaseStats> stats
  ) {
    return MonthlyStats.builder()
        .userId(userId)
        .yearMonth(yearMonth)
        .stats(stats)
        .build();
  }

}
