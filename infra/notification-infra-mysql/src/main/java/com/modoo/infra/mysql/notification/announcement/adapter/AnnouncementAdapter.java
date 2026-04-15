package com.modoo.infra.mysql.notification.announcement.adapter;

import com.modoo.application.common.dto.notification.AnnouncementCursorDto;
import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import com.modoo.application.common.port.notification.out.AnnouncementCommandPort;
import com.modoo.application.common.port.notification.out.AnnouncementLoaderPort;
import com.modoo.common.annotation.DrivenAdapter;
import com.modoo.domain.notification.announcement.aggregate.Announcement;
import com.modoo.infra.mysql.notification.announcement.entity.AnnouncementEntity;
import com.modoo.infra.mysql.notification.announcement.repository.AnnouncementRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class AnnouncementAdapter implements AnnouncementCommandPort, AnnouncementLoaderPort {

  private final AnnouncementRepository announcementRepository;

  @Override
  public Announcement save(Announcement announcement) {
    return announcementRepository.save(AnnouncementEntity.from(announcement))
        .toDomain();
  }

  @Override
  public Announcement update(Announcement announcement) {
    AnnouncementEntity announcementEntity = announcementRepository.findById(announcement.getId())
        .orElseThrow(EntityNotFoundException::new);

    announcementEntity.update(announcement);

    return announcementEntity.toDomain();
  }

  @Override
  public List<AnnouncementCursorDto> search(ScrollRequest request) {
    return announcementRepository.findAnnouncementScroll(request);
  }

  @Override
  public Announcement getAnnouncementOrElseThrow(Long id, String message) {
    return announcementRepository.findById(id)
        .orElseThrow(() -> new MissingResourceException(
            message,
            Announcement.class.getName(),
            id.toString()
        ))
        .toDomain();
  }

}
