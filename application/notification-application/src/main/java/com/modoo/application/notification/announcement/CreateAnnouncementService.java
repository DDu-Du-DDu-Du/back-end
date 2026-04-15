package com.modoo.application.notification.announcement;

import com.modoo.application.common.dto.IdResponse;
import com.modoo.application.common.dto.notification.request.CreateAnnouncementRequest;
import com.modoo.application.common.port.notification.in.CreateAnnouncementUseCase;
import com.modoo.application.common.port.notification.out.AnnouncementCommandPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.AnnouncementErrorCode;
import com.modoo.domain.notification.announcement.aggregate.Announcement;
import com.modoo.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class CreateAnnouncementService implements CreateAnnouncementUseCase {

  private final UserLoaderPort userLoaderPort;
  private final AnnouncementCommandPort announcementCommandPort;

  @Override
  public IdResponse create(Long loginId, CreateAnnouncementRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        AnnouncementErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    if (!user.isAdmin()) {
      throw new SecurityException(AnnouncementErrorCode.INVALID_AUTHORITY.getCodeName());
    }

    Announcement announcement = Announcement.builder()
        .userId(user.getId())
        .title(request.title())
        .contents(request.contents())
        .build();
    Announcement saved = announcementCommandPort.save(announcement);

    return new IdResponse(saved.getId());
  }

}
