package com.modoo.application.common.port.notification.out;

import com.modoo.application.common.dto.notification.AnnouncementCursorDto;
import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import com.modoo.domain.notification.announcement.aggregate.Announcement;
import java.util.List;

public interface AnnouncementLoaderPort {

  List<AnnouncementCursorDto> search(ScrollRequest request);

  Announcement getAnnouncementOrElseThrow(Long id, String message);

}
