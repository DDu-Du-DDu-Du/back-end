package com.ddudu.domain.user.user.service;

import com.ddudu.common.annotation.DomainService;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.domain.user.user.aggregate.enums.RandomUserAdjective;
import com.ddudu.domain.user.user.aggregate.enums.RandomUserAnimal;
import com.ddudu.domain.user.user.aggregate.vo.AuthProvider;
import com.ddudu.domain.user.user.service.dto.UserSettingsInfo;
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

  public UserSettingsInfo createUserSettingsInfo(User user) {
    return UserSettingsInfo.builder()
        .display(UserSettingsInfo.Display.builder()
            .weekStartDay(user.getWeekStartDay())
            .isDarkMode(user.isDarkMode())
            .build())
        .menuActivation(UserSettingsInfo.MenuActivation.builder()
            .calendar(UserSettingsInfo.MenuActivationItem.builder()
                .isActive(user.isActiveCalendar())
                .priority(user.getPriorityCalendar())
                .build())
            .dashboard(UserSettingsInfo.MenuActivationItem.builder()
                .isActive(user.isActiveDashboard())
                .priority(user.getPriorityDashboard())
                .build())
            .stats(UserSettingsInfo.MenuActivationItem.builder()
                .isActive(user.isActiveStats())
                .priority(user.getPriorityStats())
                .build())
            .build())
        .appConnection(UserSettingsInfo.AppConnection.builder()
            .realtimeSync(UserSettingsInfo.RealtimeSync.builder()
                .notion(user.isRealtimeSyncNotion())
                .googleCalendar(user.isRealtimeSyncGoogleCalendar())
                .microsoftTodo(user.isRealtimeSyncMicrosoftTodo())
                .build())
            .build())
        .build();
  }

}
