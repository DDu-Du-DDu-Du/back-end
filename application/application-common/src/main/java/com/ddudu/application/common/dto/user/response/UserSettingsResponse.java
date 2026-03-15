package com.ddudu.application.common.dto.user.response;

import lombok.Builder;

@Builder
public record UserSettingsResponse(
    Display display,
    MenuActivation menuActivation,
    AppConnection appConnection
) {

  public static UserSettingsResponse of(
      String weekStartDay,
      boolean isDarkMode,
      boolean isActiveCalendar,
      int priorityCalendar,
      boolean isActiveDashboard,
      int priorityDashboard,
      boolean isActiveStats,
      int priorityStats,
      boolean realtimeSyncNotion,
      boolean realtimeSyncGoogleCalendar,
      boolean realtimeSyncMicrosoftTodo
  ) {
    return UserSettingsResponse.builder()
        .display(new Display(weekStartDay, isDarkMode))
        .menuActivation(new MenuActivation(
            new MenuActivation.MenuActivationItem(isActiveCalendar, priorityCalendar),
            new MenuActivation.MenuActivationItem(isActiveDashboard, priorityDashboard),
            new MenuActivation.MenuActivationItem(isActiveStats, priorityStats)
        ))
        .appConnection(new AppConnection(
            new AppConnection.RealtimeSync(
                realtimeSyncNotion,
                realtimeSyncGoogleCalendar,
                realtimeSyncMicrosoftTodo
            )
        ))
        .build();
  }

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
