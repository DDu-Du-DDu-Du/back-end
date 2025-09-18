package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.notification.request.SaveDeviceTokenRequest;
import com.ddudu.application.common.dto.notification.response.SaveDeviceTokenResponse;

public interface SaveDeviceTokenUseCase {

  SaveDeviceTokenResponse save(Long loginId, SaveDeviceTokenRequest request);

}
