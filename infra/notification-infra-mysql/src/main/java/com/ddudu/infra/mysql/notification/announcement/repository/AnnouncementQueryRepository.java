package com.ddudu.infra.mysql.notification.announcement.repository;

import com.ddudu.application.common.dto.notification.AnnouncementCursorDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import java.util.List;

public interface AnnouncementQueryRepository {

  List<AnnouncementCursorDto> findAnnouncementScroll(ScrollRequest request);

}
