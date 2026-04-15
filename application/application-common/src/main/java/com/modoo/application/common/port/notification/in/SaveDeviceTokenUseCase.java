package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.notification.request.SaveDeviceTokenRequest;
import com.modoo.application.common.dto.notification.response.SaveDeviceTokenResponse;

public interface SaveDeviceTokenUseCase {

  SaveDeviceTokenResponse save(Long loginId, SaveDeviceTokenRequest request);

}
