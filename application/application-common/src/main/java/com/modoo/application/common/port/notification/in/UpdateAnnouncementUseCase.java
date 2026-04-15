package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.IdResponse;
import com.modoo.application.common.dto.notification.request.UpdateAnnouncementRequest;

public interface UpdateAnnouncementUseCase {

  IdResponse update(Long loginId, Long id, UpdateAnnouncementRequest request);

}
