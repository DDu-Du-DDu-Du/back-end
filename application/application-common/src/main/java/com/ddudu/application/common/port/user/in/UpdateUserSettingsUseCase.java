package com.ddudu.application.common.port.user.in;

import com.ddudu.application.common.dto.user.request.UpdateUserSettingsRequest;
import com.ddudu.application.common.dto.user.response.UserSettingsResponse;

public interface UpdateUserSettingsUseCase {

  UserSettingsResponse update(Long loginId, UpdateUserSettingsRequest request);

}
