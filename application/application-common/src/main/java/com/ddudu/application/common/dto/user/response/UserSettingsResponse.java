package com.ddudu.application.common.dto.user.response;

import com.ddudu.domain.user.user.aggregate.User;
import lombok.Builder;

@Builder
public record UserSettingsResponse(
    Display display,
    MenuActivation menuActivation,
    AppConnection appConnection
) {

  public static UserSettingsResponse from(User user) {
    return UserSettingsResponse.builder()
        .display(new Display(user.getWeekStartDay().name(), user.isDarkMode()))
        .menuActivation(new MenuActivation(
            new MenuActivation.MenuActivationItem(user.isActiveCalendar(), user.getPriorityCalendar()),
            new MenuActivation.MenuActivationItem(user.isActiveDashboard(), user.getPriorityDashboard()),
            new MenuActivation.MenuActivationItem(user.isActiveStats(), user.getPriorityStats())
        ))
        .appConnection(new AppConnection(
            new AppConnection.RealtimeSync(
                user.isRealtimeSyncNotion(),
                user.isRealtimeSyncGoogleCalendar(),
                user.isRealtimeSyncMicrosoftTodo()
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
