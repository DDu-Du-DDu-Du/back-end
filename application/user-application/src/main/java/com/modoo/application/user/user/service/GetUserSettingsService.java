package com.modoo.application.user.user.service;

import com.modoo.application.common.dto.user.response.UserSettingsResponse;
import com.modoo.application.common.port.user.in.GetUserSettingsUseCase;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.UserErrorCode;
import com.modoo.domain.user.user.aggregate.User;
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
