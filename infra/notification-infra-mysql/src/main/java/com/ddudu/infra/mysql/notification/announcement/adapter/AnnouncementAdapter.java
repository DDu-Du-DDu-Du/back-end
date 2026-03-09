package com.ddudu.infra.mysql.notification.announcement.adapter;

import com.ddudu.application.common.dto.notification.AnnouncementCursorDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.port.notification.out.AnnouncementCommandPort;
import com.ddudu.application.common.port.notification.out.AnnouncementLoaderPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.notification.announcement.aggregate.Announcement;
import com.ddudu.infra.mysql.notification.announcement.entity.AnnouncementEntity;
import com.ddudu.infra.mysql.notification.announcement.repository.AnnouncementRepository;
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
