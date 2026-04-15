package com.modoo.application.common.port.notification.out;

import com.modoo.domain.notification.briefing.aggregate.DailyBriefingLog;

public interface DailyBriefingCommandPort {

  DailyBriefingLog save(DailyBriefingLog dailyBriefingLog);

}
