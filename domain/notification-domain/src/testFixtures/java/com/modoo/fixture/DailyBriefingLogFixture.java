package com.modoo.fixture;

import com.modoo.domain.notification.briefing.aggregate.DailyBriefingLog;
import java.time.LocalDate;

public final class DailyBriefingLogFixture extends BaseFixture {

  public static DailyBriefingLog createTodayBriefing(Long userId) {
    return createDailyBriefingLog(userId, LocalDate.now());
  }

  public static DailyBriefingLog createDailyBriefingLog(Long userId, LocalDate briefingDate) {
    return DailyBriefingLog.builder()
        .userId(userId)
        .briefingDate(briefingDate)
        .build();
  }

}
