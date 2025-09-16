package com.ddudu.infra.mysql.notification.inbox.entity;

import com.ddudu.domain.notification.event.aggregate.NotificationInbox;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.infra.mysql.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
      columnDefinition = "VARCHAR",
      length = 20
  )
  @Enumerated(EnumType.STRING)
  private NotificationEventTypeCode typeCode;

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

  @Column(
      name = "context_id",
      nullable = false
  )
  private Long contextId;

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
        .contextId(domain.getContextId())
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
        .contextId(contextId)
        .build();
  }

}
