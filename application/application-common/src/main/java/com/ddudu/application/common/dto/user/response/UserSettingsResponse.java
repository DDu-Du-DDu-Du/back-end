package com.ddudu.application.common.dto.user.response;

import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import lombok.Builder;

@Builder
public record UserSettingsResponse(
    Display display,
    MenuActivation menuActivation,
    AppConnection appConnection
) {

  public static UserSettingsResponse from(
      WeekStartDay weekStartDay,
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
        .display(Display.builder()
            .weekStartDay(weekStartDay)
            .isDarkMode(isDarkMode)
            .build())
        .menuActivation(MenuActivation.builder()
            .calendar(MenuActivationItem.builder()
                .isActive(isActiveCalendar)
                .priority(priorityCalendar)
                .build())
            .dashboard(MenuActivationItem.builder()
                .isActive(isActiveDashboard)
                .priority(priorityDashboard)
                .build())
            .stats(MenuActivationItem.builder()
                .isActive(isActiveStats)
                .priority(priorityStats)
                .build())
            .build())
        .appConnection(AppConnection.builder()
            .realtimeSync(RealtimeSync.builder()
                .notion(realtimeSyncNotion)
                .googleCalendar(realtimeSyncGoogleCalendar)
                .microsoftTodo(realtimeSyncMicrosoftTodo)
                .build())
            .build())
        .build();
  }

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
