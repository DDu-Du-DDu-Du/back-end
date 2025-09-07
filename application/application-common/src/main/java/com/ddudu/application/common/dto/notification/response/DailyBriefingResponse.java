package com.ddudu.application.common.dto.notification.response;

import com.ddudu.application.common.dto.notification.DailyBriefingDto;
import lombok.Builder;

@Builder
public record DailyBriefingResponse(boolean isFirst, DailyBriefingDto content) {

  public static DailyBriefingResponse notFirst() {
    return DailyBriefingResponse.builder()
        .isFirst(false)
        .build();
  }

  public static DailyBriefingResponse from(DailyBriefingDto content) {
    return DailyBriefingResponse.builder()
        .isFirst(true)
        .content(content)
        .build();
  }

}
