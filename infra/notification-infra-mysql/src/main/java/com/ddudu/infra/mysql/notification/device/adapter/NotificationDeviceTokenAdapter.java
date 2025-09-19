package com.ddudu.infra.mysql.notification.device.adapter;

import com.ddudu.application.common.port.notification.out.NotificationDeviceTokenCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationDeviceTokenLoaderPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.notification.device.aggregate.NotificationDeviceToken;
import com.ddudu.infra.mysql.notification.device.entity.NotificationDeviceTokenEntity;
import com.ddudu.infra.mysql.notification.device.repository.NotificationDeviceTokenRepository;
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

}
