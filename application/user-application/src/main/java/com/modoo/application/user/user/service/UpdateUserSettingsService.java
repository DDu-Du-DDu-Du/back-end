package com.modoo.application.user.user.service;

import com.modoo.application.common.dto.user.request.UpdateUserSettingsRequest;
import com.modoo.application.common.dto.user.response.UserSettingsResponse;
import com.modoo.application.common.port.user.in.UpdateUserSettingsUseCase;
import com.modoo.application.common.port.user.out.UserCommandPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.UserErrorCode;
import com.modoo.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateUserSettingsService implements UpdateUserSettingsUseCase {

  private final UserLoaderPort userLoaderPort;
  private final UserCommandPort userCommandPort;

  @Override
  public UserSettingsResponse update(Long loginId, UpdateUserSettingsRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        UserErrorCode.NO_TARGET_FOR_MY_INFO.getCodeName()
    );
    User updated = user.updateOptions(
        request.display()
            .weekStartDay(),
        request.display()
            .isDarkMode(),
        request.menuActivation()
            .calendar()
            .isActive(),
        request.menuActivation()
            .calendar()
            .priority(),
        request.menuActivation()
            .dashboard()
            .isActive(),
        request.menuActivation()
            .dashboard()
            .priority(),
        request.menuActivation()
            .stats()
            .isActive(),
        request.menuActivation()
            .stats()
            .priority(),
        request.appConnection()
            .realtimeSync()
            .notion(),
        request.appConnection()
            .realtimeSync()
            .googleCalendar(),
        request.appConnection()
            .realtimeSync()
            .microsoftTodo()
    );
    User saved = userCommandPort.update(updated);

    return UserSettingsResponse.from(
        saved.getWeekStartDay(),
        saved.isDarkMode(),
        saved.isActiveCalendar(),
        saved.getPriorityCalendar(),
        saved.isActiveDashboard(),
        saved.getPriorityDashboard(),
        saved.isActiveStats(),
        saved.getPriorityStats(),
        saved.isRealtimeSyncNotion(),
        saved.isRealtimeSyncGoogleCalendar(),
        saved.isRealtimeSyncMicrosoftTodo()
    );
  }

}
