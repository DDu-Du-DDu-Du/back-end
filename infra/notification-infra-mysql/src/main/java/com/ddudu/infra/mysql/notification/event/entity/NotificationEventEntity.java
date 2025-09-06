package com.ddudu.infra.mysql.notification.event.entity;

import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
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
@Table(name = "notification_events")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class NotificationEventEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "type_code",
      nullable = false,
      length = 20,
      columnDefinition = "VARCHAR"
  )
  @Enumerated(EnumType.STRING)
  private NotificationEventTypeCode typeCode;

  @Column(name = "sender_id")
  private Long senderId;

  @Column(
      name = "receiver_id",
      nullable = false
  )
  private Long receiverId;

  @Column(
      name = "context_id",
      nullable = false
  )
  private Long contextId;

  @Column(
      name = "will_fire_at",
      nullable = false
  )
  private LocalDateTime willFireAt;

  @Column(
      name = "fired_at",
      columnDefinition = "TIMESTAMP"
  )
  private LocalDateTime firedAt;

  public static NotificationEventEntity from(NotificationEvent domain) {
    return NotificationEventEntity.builder()
        .id(domain.getId())
        .typeCode(domain.getTypeCode())
        .senderId(domain.getSenderId())
        .receiverId(domain.getReceiverId())
        .contextId(domain.getContextId())
        .willFireAt(domain.getWillFireAt())
        .firedAt(domain.getFiredAt())
        .build();
  }

  public NotificationEvent toDomain() {
    return NotificationEvent.builder()
        .id(id)
        .typeCode(typeCode)
        .senderId(senderId)
        .receiverId(receiverId)
        .contextId(contextId)
        .firedAt(firedAt)
        .willFireAt(willFireAt)
        .build();
  }

  public void update(NotificationEvent domain) {
    this.typeCode = domain.getTypeCode();
    this.contextId = domain.getContextId();
    this.senderId = domain.getSenderId();
    this.receiverId = domain.getReceiverId();
    this.firedAt = domain.getFiredAt();
    this.willFireAt = domain.getWillFireAt();
  }

}
