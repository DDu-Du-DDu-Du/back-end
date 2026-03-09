package com.ddudu.application.notification.announcement;

import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.notification.request.UpdateAnnouncementRequest;
import com.ddudu.application.common.port.notification.in.UpdateAnnouncementUseCase;
import com.ddudu.application.common.port.notification.out.AnnouncementCommandPort;
import com.ddudu.application.common.port.notification.out.AnnouncementLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.AnnouncementErrorCode;
import com.ddudu.domain.notification.announcement.aggregate.Announcement;
import com.ddudu.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateAnnouncementService implements UpdateAnnouncementUseCase {

  private final UserLoaderPort userLoaderPort;
  private final AnnouncementLoaderPort announcementLoaderPort;
  private final AnnouncementCommandPort announcementCommandPort;

  @Override
  public IdResponse update(Long loginId, Long id, UpdateAnnouncementRequest request) {
    User loginUser = userLoaderPort.getUserOrElseThrow(
        loginId,
        AnnouncementErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    if (!loginUser.isAdmin()) {
      throw new SecurityException(AnnouncementErrorCode.INVALID_AUTHORITY.getCodeName());
    }

    Announcement found = announcementLoaderPort.getAnnouncementOrElseThrow(
        id,
        AnnouncementErrorCode.ANNOUNCEMENT_NOT_EXISTING.getCodeName()
    );
    if (!found.isAuthor(loginUser.getId())) {
      throw new SecurityException(AnnouncementErrorCode.INVALID_AUTHORITY.getCodeName());
    }

    Announcement updated = found.update(request.title(), request.body());

    Announcement saved = announcementCommandPort.update(updated);
    return new IdResponse(saved.getId());
  }

}
