package com.ddudu.domain.user.user.service;

import com.ddudu.common.annotation.DomainService;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.RandomUserAdjective;
import com.ddudu.domain.user.user.aggregate.enums.RandomUserAnimal;
import com.ddudu.domain.user.user.aggregate.enums.WeekStartDay;
import com.ddudu.domain.user.user.aggregate.vo.AppConnectionOptions;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.domain.user.user.aggregate.vo.DisplayOptions;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationItem;
import com.ddudu.domain.user.user.aggregate.vo.MenuActivationOptions;
import com.ddudu.domain.user.user.aggregate.vo.Options;
import com.ddudu.domain.user.user.aggregate.vo.RealtimeSyncOptions;
import java.util.Collections;
import java.util.UUID;

@DomainService
public class UserDomainService {

  public User createFirstUser(AuthProvider authProvider) {
    String lowTime = UUID.randomUUID()
        .toString()
        .substring(0, 8);
    RandomUserAdjective adjective = RandomUserAdjective.getRandom();
    RandomUserAnimal animal = RandomUserAnimal.getRandom();
    String username = adjective.getUsername() + animal.getUsername() + lowTime;
    String nickname = adjective.getNickname() + " " + animal.getNickname();

    return User.builder()
        .username(username)
        .nickname(nickname)
        .authProviders(Collections.singletonList(authProvider))
        .build();
  }

  public Options buildUpdatedOptions(
      User user,
      String weekStartDay,
      boolean isDarkMode,
      boolean isActiveCalendar,
      int priorityCalendar,
      boolean isActiveDashboard,
      int priorityDashboard,
      boolean isActiveStats,
      int priorityStats,
      boolean notion,
      boolean googleCalendar,
      boolean microsoftTodo
  ) {
    return Options.builder()
        .allowingFollowsAfterApproval(user.isAllowingFollowsAfterApproval())
        .templateNotification(user.isNotifyingTemplate())
        .dduduNotification(user.isNotifyingDdudu())
        .display(DisplayOptions.builder()
            .weekStartDay(WeekStartDay.get(weekStartDay))
            .darkMode(isDarkMode)
            .build())
        .menuActivation(MenuActivationOptions.builder()
            .calendar(MenuActivationItem.builder()
                .active(isActiveCalendar)
                .priority(priorityCalendar)
                .build())
            .dashboard(MenuActivationItem.builder()
                .active(isActiveDashboard)
                .priority(priorityDashboard)
                .build())
            .stats(MenuActivationItem.builder()
                .active(isActiveStats)
                .priority(priorityStats)
                .build())
            .build())
        .appConnection(AppConnectionOptions.builder()
            .realtimeSync(RealtimeSyncOptions.builder()
                .notion(notion)
                .googleCalendar(googleCalendar)
                .microsoftTodo(microsoftTodo)
                .build())
            .build())
        .build();
  }

}
