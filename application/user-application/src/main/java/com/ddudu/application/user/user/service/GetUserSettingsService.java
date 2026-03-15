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

    return UserSettingsResponse.from(
        user.getWeekStartDay(),
        user.isDarkMode(),
        user.isActiveCalendar(),
        user.getPriorityCalendar(),
        user.isActiveDashboard(),
        user.getPriorityDashboard(),
        user.isActiveStats(),
        user.getPriorityStats(),
        user.isRealtimeSyncNotion(),
        user.isRealtimeSyncGoogleCalendar(),
        user.isRealtimeSyncMicrosoftTodo()
    );
  }

}
