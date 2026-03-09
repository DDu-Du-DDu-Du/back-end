package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.notification.request.UpdateAnnouncementRequest;

public interface UpdateAnnouncementUseCase {

  IdResponse update(Long loginId, Long id, UpdateAnnouncementRequest request);

}
