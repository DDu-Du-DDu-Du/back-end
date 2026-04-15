package com.modoo.application.notification.announcement;

import com.modoo.application.common.dto.notification.response.AnnouncementDetailResponse;
import com.modoo.application.common.port.notification.in.RetrieveAnnouncementUseCase;
import com.modoo.application.common.port.notification.out.AnnouncementLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.AnnouncementErrorCode;
import com.modoo.common.exception.UserErrorCode;
import com.modoo.domain.notification.announcement.aggregate.Announcement;
import com.modoo.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveAnnouncementService implements RetrieveAnnouncementUseCase {

  private final AnnouncementLoaderPort announcementLoaderPort;
  private final UserLoaderPort userLoaderPort;

  @Override
  public AnnouncementDetailResponse findById(Long id) {
    Announcement announcement = announcementLoaderPort.getAnnouncementOrElseThrow(
        id,
        AnnouncementErrorCode.ANNOUNCEMENT_NOT_EXISTING.getCodeName()
    );
    User author = userLoaderPort.getUserOrElseThrow(
        announcement.getUserId(),
        UserErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    return new AnnouncementDetailResponse(
        announcement.getId(),
        announcement.getTitle(),
        announcement.getContents(),
        announcement.getCreatedAt(),
        author.getNickname()
    );
  }

}
