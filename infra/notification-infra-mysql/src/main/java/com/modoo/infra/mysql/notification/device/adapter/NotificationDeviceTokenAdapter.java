package com.modoo.infra.mysql.notification.device.adapter;

import com.modoo.application.common.port.notification.out.NotificationDeviceTokenCommandPort;
import com.modoo.application.common.port.notification.out.NotificationDeviceTokenLoaderPort;
import com.modoo.common.annotation.DrivenAdapter;
import com.modoo.domain.notification.device.aggregate.NotificationDeviceToken;
import com.modoo.domain.notification.device.aggregate.enums.DeviceChannel;
import com.modoo.infra.mysql.notification.device.entity.NotificationDeviceTokenEntity;
import com.modoo.infra.mysql.notification.device.repository.NotificationDeviceTokenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class NotificationDeviceTokenAdapter implements NotificationDeviceTokenCommandPort,
    NotificationDeviceTokenLoaderPort {

  private final NotificationDeviceTokenRepository notificationDeviceTokenRepository;

  @Override
  public NotificationDeviceToken save(NotificationDeviceToken deviceToken) {
    return notificationDeviceTokenRepository.save(NotificationDeviceTokenEntity.from(deviceToken))
        .toDomain();
  }

  @Override
  public List<NotificationDeviceToken> getAllTokensOfUser(Long userId) {
    return notificationDeviceTokenRepository.findAllByUserId(userId)
        .stream()
        .map(NotificationDeviceTokenEntity::toDomain)
        .toList();
  }

  @Override
  public List<NotificationDeviceToken> getTokensOfUserByChannel(
      Long userId,
      DeviceChannel channel
  ) {
    return notificationDeviceTokenRepository.findAllByUserIdAndChannel(userId, channel)
        .stream()
        .map(NotificationDeviceTokenEntity::toDomain)
        .toList();
  }

}
