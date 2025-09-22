package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.notification.request.NotificationInboxSearchRequest;
import com.ddudu.application.common.dto.notification.response.NotificationInboxSearchResponse;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;

public interface NotificationInboxSearchUseCase {

  ScrollResponse<NotificationInboxSearchResponse> search(
      Long loginId,
      NotificationInboxSearchRequest request
  );

}
