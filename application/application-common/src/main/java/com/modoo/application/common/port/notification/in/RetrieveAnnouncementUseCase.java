package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.notification.response.AnnouncementDetailResponse;

public interface RetrieveAnnouncementUseCase {

  AnnouncementDetailResponse findById(Long id);

}
