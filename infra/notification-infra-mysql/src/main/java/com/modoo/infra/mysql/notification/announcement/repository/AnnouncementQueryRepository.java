package com.modoo.infra.mysql.notification.announcement.repository;

import com.modoo.application.common.dto.notification.AnnouncementCursorDto;
import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import java.util.List;

public interface AnnouncementQueryRepository {

  List<AnnouncementCursorDto> findAnnouncementScroll(ScrollRequest request);

}
