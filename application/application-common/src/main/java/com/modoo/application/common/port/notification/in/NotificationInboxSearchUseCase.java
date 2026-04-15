package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.notification.request.NotificationInboxSearchRequest;
import com.modoo.application.common.dto.notification.response.NotificationInboxSearchResponse;
import com.modoo.application.common.dto.scroll.response.ScrollResponse;

public interface NotificationInboxSearchUseCase {

  ScrollResponse<NotificationInboxSearchResponse> search(
      Long loginId,
      NotificationInboxSearchRequest request
  );

}
