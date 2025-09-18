package com.ddudu.infra.mysql.notification.device.entity;

import com.ddudu.domain.notification.device.aggregate.NotificationDeviceToken;
import com.ddudu.domain.notification.device.aggregate.enums.DeviceChannel;
import com.ddudu.infra.mysql.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "notification_device_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class NotificationDeviceTokenEntity extends BaseEntity {

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
      name = "channel",
      nullable = false,
      length = 16,
      columnDefinition = "VARCHAR"
  )
  @Enumerated(EnumType.STRING)
  private DeviceChannel channel;

  @Column(
      name = "token",
      nullable = false,
      length = 512
  )
  private String token;

  public static NotificationDeviceTokenEntity from(NotificationDeviceToken domain) {
    return NotificationDeviceTokenEntity.builder()
        .id(domain.getId())
        .userId(domain.getUserId())
        .channel(domain.getChannel())
        .token(domain.getToken())
        .build();
  }

  public NotificationDeviceToken toDomain() {
    return NotificationDeviceToken.builder()
        .id(id)
        .userId(userId)
        .channel(channel)
        .token(token)
        .build();
  }

}
