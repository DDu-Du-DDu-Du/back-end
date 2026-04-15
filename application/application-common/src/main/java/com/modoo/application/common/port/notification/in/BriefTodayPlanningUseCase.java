package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.notification.response.DailyBriefingResponse;

public interface BriefTodayPlanningUseCase {

  DailyBriefingResponse getDailyBriefing(Long loginId);

}
