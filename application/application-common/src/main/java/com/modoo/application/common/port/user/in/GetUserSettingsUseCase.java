package com.modoo.application.common.port.user.in;

import com.modoo.application.common.dto.user.response.UserSettingsResponse;

public interface GetUserSettingsUseCase {

  UserSettingsResponse getUserSettings(Long loginId);

}
