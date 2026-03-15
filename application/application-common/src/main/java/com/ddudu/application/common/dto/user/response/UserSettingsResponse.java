package com.ddudu.application.common.dto.user.response;

import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import lombok.Builder;

@Builder
public record UserSettingsResponse(
    Display display,
    MenuActivation menuActivation,
    AppConnection appConnection
) {

  @Builder
  public record Display(
      WeekStartDay weekStartDay,
      boolean isDarkMode
  ) {

  }

  @Builder
  public record MenuActivation(
      MenuActivationItem calendar,
      MenuActivationItem dashboard,
      MenuActivationItem stats
  ) {

  }

  @Builder
  public record MenuActivationItem(
      boolean isActive,
      int priority
  ) {

  }

  @Builder
  public record AppConnection(
      RealtimeSync realtimeSync
  ) {

  }

  @Builder
  public record RealtimeSync(
      boolean notion,
      boolean googleCalendar,
      boolean microsoftTodo
  ) {

  }

}
