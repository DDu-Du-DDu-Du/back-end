package com.ddudu.application.common.port.notification.out;

import com.ddudu.domain.notification.briefing.aggregate.DailyBriefingLog;

public interface DailyBriefingCommandPort {

  DailyBriefingLog save(DailyBriefingLog dailyBriefingLog);

}
