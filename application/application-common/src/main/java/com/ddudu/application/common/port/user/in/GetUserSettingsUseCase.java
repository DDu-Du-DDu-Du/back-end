package com.ddudu.application.common.port.user.in;

import com.ddudu.application.common.dto.user.response.UserSettingsResponse;

public interface GetUserSettingsUseCase {

  UserSettingsResponse getUserSettings(Long loginId);

}
