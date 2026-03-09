package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.notification.SimpleAnnouncementDto;
import com.ddudu.application.common.dto.notification.request.AnnouncementSearchRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;

public interface AnnouncementSearchUseCase {

  ScrollResponse<SimpleAnnouncementDto> search(AnnouncementSearchRequest request);

}
