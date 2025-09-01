package com.ddudu.infra.mysql.notification.inbox.entity;

import com.ddudu.domain.notification.inbox.aggregate.NotificationInbox;
import com.ddudu.infra.mysql.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_inboxes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class NotificationInboxEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "user_id",
      nullable = false
  )
  private Long userId;

  @Column(name = "sender_id")
  private Long senderId;

  @Column(
      name = "event_id",
      nullable = false
  )
  private Long eventId;

  @Column(
      name = "type_code",
      nullable = false,
      length = 20
  )
  private String typeCode;

  @Column(
      name = "title",
      length = 50
  )
  private String title;

  @Column(
      name = "body",
      length = 200
  )
  private String body;

  @Column(
      name = "read_at",
      columnDefinition = "TIMESTAMP"
  )
  private LocalDateTime readAt;

  public static NotificationInboxEntity from(NotificationInbox domain) {
    return NotificationInboxEntity.builder()
        .id(domain.getId())
        .userId(domain.getUserId())
        .senderId(domain.getSenderId())
        .eventId(domain.getEventId())
        .typeCode(domain.getTypeCode())
        .title(domain.getTitle())
        .body(domain.getBody())
        .readAt(domain.getReadAt())
        .build();
  }

  public NotificationInbox toDomain() {
    return NotificationInbox.builder()
        .id(id)
        .userId(userId)
        .senderId(senderId)
        .eventId(eventId)
        .typeCode(typeCode)
        .title(title)
        .body(body)
        .readAt(readAt)
        .build();
  }

}
