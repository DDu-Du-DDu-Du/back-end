package com.ddudu.application.user.user.service;

import com.ddudu.application.common.dto.user.response.UserSettingsResponse;
import com.ddudu.application.common.port.user.in.GetUserSettingsUseCase;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.UserErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetUserSettingsService implements GetUserSettingsUseCase {

  private final UserLoaderPort userLoaderPort;

  @Override
  public UserSettingsResponse getUserSettings(Long loginId) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        UserErrorCode.NO_TARGET_FOR_MY_INFO.getCodeName()
    );

    return toResponse(user);
  }

  private UserSettingsResponse toResponse(User user) {
    return UserSettingsResponse.builder()
        .display(UserSettingsResponse.Display.builder()
            .weekStartDay(user.getWeekStartDay())
            .isDarkMode(user.isDarkMode())
            .build())
        .menuActivation(UserSettingsResponse.MenuActivation.builder()
            .calendar(UserSettingsResponse.MenuActivationItem.builder()
                .isActive(user.isActiveCalendar())
                .priority(user.getPriorityCalendar())
                .build())
            .dashboard(UserSettingsResponse.MenuActivationItem.builder()
                .isActive(user.isActiveDashboard())
                .priority(user.getPriorityDashboard())
                .build())
            .stats(UserSettingsResponse.MenuActivationItem.builder()
                .isActive(user.isActiveStats())
                .priority(user.getPriorityStats())
                .build())
            .build())
        .appConnection(UserSettingsResponse.AppConnection.builder()
            .realtimeSync(UserSettingsResponse.RealtimeSync.builder()
                .notion(user.isRealtimeSyncNotion())
                .googleCalendar(user.isRealtimeSyncGoogleCalendar())
                .microsoftTodo(user.isRealtimeSyncMicrosoftTodo())
                .build())
            .build())
        .build();
  }

}
