package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.IdResponse;
import com.modoo.application.common.dto.notification.request.CreateAnnouncementRequest;

public interface CreateAnnouncementUseCase {

  IdResponse create(Long loginId, CreateAnnouncementRequest request);

}
