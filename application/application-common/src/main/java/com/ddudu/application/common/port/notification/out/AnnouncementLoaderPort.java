package com.ddudu.application.common.port.notification.out;

import com.ddudu.application.common.dto.notification.AnnouncementCursorDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.domain.notification.announcement.aggregate.Announcement;
import java.util.List;

public interface AnnouncementLoaderPort {

  List<AnnouncementCursorDto> search(ScrollRequest request);

  Announcement getAnnouncementOrElseThrow(Long id, String message);

}
