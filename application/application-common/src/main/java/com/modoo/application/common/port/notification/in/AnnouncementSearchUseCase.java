package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.notification.SimpleAnnouncementDto;
import com.modoo.application.common.dto.notification.request.AnnouncementSearchRequest;
import com.modoo.application.common.dto.scroll.response.ScrollResponse;

public interface AnnouncementSearchUseCase {

  ScrollResponse<SimpleAnnouncementDto> search(AnnouncementSearchRequest request);

}
