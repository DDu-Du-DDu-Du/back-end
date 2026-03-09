package com.ddudu.application.notification.announcement;

import com.ddudu.application.common.dto.IdResponse;
import com.ddudu.application.common.dto.notification.request.CreateAnnouncementRequest;
import com.ddudu.application.common.port.notification.in.CreateAnnouncementUseCase;
import com.ddudu.application.common.port.notification.out.AnnouncementCommandPort;
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
public class CreateAnnouncementService implements CreateAnnouncementUseCase {

  private final UserLoaderPort userLoaderPort;
  private final AnnouncementCommandPort announcementCommandPort;

  @Override
  public IdResponse create(Long loginId, CreateAnnouncementRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        AnnouncementErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Announcement announcement = Announcement.builder()
        .userId(user.getId())
        .title(request.title())
        .contents(request.contents())
        .build();
    Announcement saved = announcementCommandPort.save(announcement);

    return new IdResponse(saved.getId());
  }

}
