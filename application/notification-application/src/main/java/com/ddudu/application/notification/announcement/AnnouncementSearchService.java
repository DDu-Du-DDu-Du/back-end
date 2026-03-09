package com.ddudu.application.notification.announcement;

import com.ddudu.application.common.dto.notification.AnnouncementCursorDto;
import com.ddudu.application.common.dto.notification.SimpleAnnouncementDto;
import com.ddudu.application.common.dto.notification.request.AnnouncementSearchRequest;
import com.ddudu.application.common.dto.scroll.response.ScrollResponse;
import com.ddudu.application.common.port.notification.in.AnnouncementSearchUseCase;
import com.ddudu.application.common.port.notification.out.AnnouncementLoaderPort;
import com.ddudu.common.annotation.UseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementSearchService implements AnnouncementSearchUseCase {

  private final AnnouncementLoaderPort announcementLoaderPort;

  @Override
  public ScrollResponse<SimpleAnnouncementDto> search(AnnouncementSearchRequest request) {
    List<AnnouncementCursorDto> announcementsWithCursor = announcementLoaderPort.search(
        request.getScroll()
    );

    return getScrollResponse(announcementsWithCursor, request.getSize());
  }

  private ScrollResponse<SimpleAnnouncementDto> getScrollResponse(
      List<AnnouncementCursorDto> announcementsWithCursor,
      int size
  ) {
    List<SimpleAnnouncementDto> announcements = announcementsWithCursor.stream()
        .limit(size)
        .map(AnnouncementCursorDto::simpleAnnouncement)
        .toList();
    String nextCursor = getNextCursor(announcementsWithCursor, size);

    return ScrollResponse.from(announcements, nextCursor);
  }

  private String getNextCursor(List<AnnouncementCursorDto> announcementsWithCursor, int size) {
    if (announcementsWithCursor.size() > size) {
      return announcementsWithCursor.get(size - 1)
          .cursor();
    }

    return null;
  }

}
