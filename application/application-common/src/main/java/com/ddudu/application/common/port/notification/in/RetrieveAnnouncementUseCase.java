package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.notification.response.AnnouncementDetailResponse;

public interface RetrieveAnnouncementUseCase {

  AnnouncementDetailResponse findById(Long id);

}
