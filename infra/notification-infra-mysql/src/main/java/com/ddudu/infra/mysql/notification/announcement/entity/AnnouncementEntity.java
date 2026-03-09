package com.ddudu.infra.mysql.notification.announcement.entity;

import com.ddudu.domain.notification.announcement.aggregate.Announcement;
import com.ddudu.infra.mysql.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "announcements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class AnnouncementEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "user_id",
      nullable = false
  )
  private Long userId;

  @Column(
      name = "title",
      nullable = false,
      length = 50
  )
  private String title;

  @Column(
      name = "contents",
      nullable = false,
      length = 2000
  )
  private String contents;

  public static AnnouncementEntity from(Announcement domain) {
    return AnnouncementEntity.builder()
        .id(domain.getId())
        .userId(domain.getUserId())
        .title(domain.getTitle())
        .contents(domain.getContents())
        .build();
  }

  public Announcement toDomain() {
    return Announcement.builder()
        .id(id)
        .userId(userId)
        .title(title)
        .contents(contents)
        .createdAt(getCreatedAt())
        .build();
  }

}
