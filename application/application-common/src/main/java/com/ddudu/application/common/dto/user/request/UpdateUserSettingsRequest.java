package com.ddudu.application.common.dto.user.request;

import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import com.ddudu.domain.user.user.aggregate.vo.AppConnectionOptions;
import com.ddudu.domain.user.user.aggregate.vo.DisplayOptions;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationItem;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationOptions;
import com.ddudu.domain.user.user.aggregate.vo.Options;
import com.ddudu.domain.user.user.aggregate.vo.RealtimeSyncOptions;
import lombok.Builder;

@Builder
public record UpdateUserSettingsRequest(
    Display display,
    MenuActivation menuActivation,
    AppConnection appConnection
) {

  public Options toOptions(
      boolean allowingFollowsAfterApproval,
      boolean templateNotification,
      boolean dduduNotification
  ) {
    return Options.builder()
        .allowingFollowsAfterApproval(allowingFollowsAfterApproval)
        .templateNotification(templateNotification)
        .dduduNotification(dduduNotification)
        .display(DisplayOptions.builder()
            .weekStartDay(WeekStartDay.get(display.weekStartDay()))
            .darkMode(display.isDarkMode())
            .build())
        .menuActivation(MenuActivationOptions.builder()
            .calendar(MenuActivationItem.builder()
                .active(menuActivation.calendar().isActive())
                .priority(menuActivation.calendar().priority())
                .build())
            .dashboard(MenuActivationItem.builder()
                .active(menuActivation.dashboard().isActive())
                .priority(menuActivation.dashboard().priority())
                .build())
            .stats(MenuActivationItem.builder()
                .active(menuActivation.stats().isActive())
                .priority(menuActivation.stats().priority())
                .build())
            .build())
        .appConnection(AppConnectionOptions.builder()
            .realtimeSync(RealtimeSyncOptions.builder()
                .notion(appConnection.realtimeSync().notion())
                .googleCalendar(appConnection.realtimeSync().googleCalendar())
                .microsoftTodo(appConnection.realtimeSync().microsoftTodo())
                .build())
            .build())
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
