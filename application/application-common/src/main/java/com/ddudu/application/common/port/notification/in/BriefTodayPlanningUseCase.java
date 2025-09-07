package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.notification.response.DailyBriefingResponse;

public interface BriefTodayPlanningUseCase {

  DailyBriefingResponse getDailyBriefing(Long loginId);

}
