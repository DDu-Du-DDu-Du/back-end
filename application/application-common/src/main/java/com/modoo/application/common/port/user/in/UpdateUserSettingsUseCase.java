package com.modoo.application.common.port.user.in;

import com.modoo.application.common.dto.user.request.UpdateUserSettingsRequest;
import com.modoo.application.common.dto.user.response.UserSettingsResponse;

public interface UpdateUserSettingsUseCase {

  UserSettingsResponse update(Long loginId, UpdateUserSettingsRequest request);

}
