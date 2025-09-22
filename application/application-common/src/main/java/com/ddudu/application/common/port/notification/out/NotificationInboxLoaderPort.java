package com.ddudu.application.common.port.notification.out;

import com.ddudu.application.common.dto.notification.NotificationInboxCursorDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import java.util.List;

public interface NotificationInboxLoaderPort {

  List<NotificationInboxCursorDto> search(Long userId, ScrollRequest request);

}
