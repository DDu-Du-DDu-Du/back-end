package com.ddudu.fixture;

import com.ddudu.domain.notification.announcement.aggregate.Announcement;
import java.time.LocalDateTime;

public final class AnnouncementFixture extends BaseFixture {

  public static Announcement createValidAnnouncement() {
    return createAnnouncement(
        getRandomId(),
        getRandomAnnouncementTitle(),
        getRandomAnnouncementContents(),
        getRandomId(),
        getRandomDateTime()
    );
  }

  public static Announcement createAnnouncementWithTitle(String title) {
    Announcement validAnnouncement = createValidAnnouncement();

    return createAnnouncement(
        validAnnouncement.getId(),
        title,
        validAnnouncement.getContents(),
        validAnnouncement.getUserId(),
        validAnnouncement.getCreatedAt()
    );
  }

  public static Announcement createAnnouncementWithContents(String contents) {
    Announcement validAnnouncement = createValidAnnouncement();

    return createAnnouncement(
        validAnnouncement.getId(),
        validAnnouncement.getTitle(),
        contents,
        validAnnouncement.getUserId(),
        validAnnouncement.getCreatedAt()
    );
  }

  public static Announcement createAnnouncementWithUserId(Long userId) {
    Announcement validAnnouncement = createValidAnnouncement();

    return createAnnouncement(
        validAnnouncement.getId(),
        validAnnouncement.getTitle(),
        validAnnouncement.getContents(),
        userId,
        validAnnouncement.getCreatedAt()
    );
  }

  public static Announcement createAnnouncementWithCreatedAt(LocalDateTime createdAt) {
    Announcement validAnnouncement = createValidAnnouncement();

    return createAnnouncement(
        validAnnouncement.getId(),
        validAnnouncement.getTitle(),
        validAnnouncement.getContents(),
        validAnnouncement.getUserId(),
        createdAt
    );
  }

  public static Announcement createAnnouncement(
      Long id,
      String title,
      String contents,
      Long userId,
      LocalDateTime createdAt
  ) {
    return Announcement.builder()
        .id(id)
        .title(title)
        .contents(contents)
        .userId(userId)
        .createdAt(createdAt)
        .build();
  }

  public static String getRandomAnnouncementTitle() {
    return getRandomSentenceWithMax(50);
  }

  public static String getRandomAnnouncementContents() {
    return getRandomSentenceWithMax(2000);
  }

}
