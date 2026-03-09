package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.notification.request.CreateAnnouncementRequest;

public interface CreateAnnouncementUseCase {

  IdResponse create(Long loginId, CreateAnnouncementRequest request);

}
