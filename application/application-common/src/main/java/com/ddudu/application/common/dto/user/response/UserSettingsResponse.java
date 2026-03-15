package com.ddudu.application.common.dto.user.response;

import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import com.ddudu.domain.user.user.service.dto.UserSettingsInfo;
import lombok.Builder;

@Builder
public record UserSettingsResponse(
    Display display,
    MenuActivation menuActivation,
    AppConnection appConnection
) {

  public static UserSettingsResponse from(UserSettingsInfo settingsInfo) {
    return UserSettingsResponse.builder()
        .display(Display.builder()
            .weekStartDay(settingsInfo.display().weekStartDay())
            .isDarkMode(settingsInfo.display().isDarkMode())
            .build())
        .menuActivation(MenuActivation.builder()
            .calendar(MenuActivationItem.builder()
                .isActive(settingsInfo.menuActivation().calendar().isActive())
                .priority(settingsInfo.menuActivation().calendar().priority())
                .build())
            .dashboard(MenuActivationItem.builder()
                .isActive(settingsInfo.menuActivation().dashboard().isActive())
                .priority(settingsInfo.menuActivation().dashboard().priority())
                .build())
            .stats(MenuActivationItem.builder()
                .isActive(settingsInfo.menuActivation().stats().isActive())
                .priority(settingsInfo.menuActivation().stats().priority())
                .build())
            .build())
        .appConnection(AppConnection.builder()
            .realtimeSync(RealtimeSync.builder()
                .notion(settingsInfo.appConnection().realtimeSync().notion())
                .googleCalendar(settingsInfo.appConnection().realtimeSync().googleCalendar())
                .microsoftTodo(settingsInfo.appConnection().realtimeSync().microsoftTodo())
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
