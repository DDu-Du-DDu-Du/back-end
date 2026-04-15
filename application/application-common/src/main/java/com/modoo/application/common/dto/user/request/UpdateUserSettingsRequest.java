package com.modoo.application.common.dto.user.request;

import lombok.Builder;

@Builder
public record UpdateUserSettingsRequest(
    Display display,
    MenuActivation menuActivation,
    AppConnection appConnection
) {

  public record Display(
      String weekStartDay,
      boolean isDarkMode
  ) {

  }

  public record MenuActivation(
      MenuActivationItem calendar,
      MenuActivationItem dashboard,
      MenuActivationItem stats
  ) {

    public record MenuActivationItem(
        boolean isActive,
        int priority
    ) {

    }

  }

  public record AppConnection(
      RealtimeSync realtimeSync
  ) {

    public record RealtimeSync(
        boolean notion,
        boolean googleCalendar,
        boolean microsoftTodo
    ) {

    }

  }

}
