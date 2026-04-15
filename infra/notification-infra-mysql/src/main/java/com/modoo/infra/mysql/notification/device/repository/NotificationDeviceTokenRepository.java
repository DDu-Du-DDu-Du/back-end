package com.modoo.infra.mysql.notification.device.repository;

import com.modoo.domain.notification.device.aggregate.enums.DeviceChannel;
import com.modoo.infra.mysql.notification.device.entity.NotificationDeviceTokenEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationDeviceTokenRepository extends
    JpaRepository<NotificationDeviceTokenEntity, Long> {

  List<NotificationDeviceTokenEntity> findAllByUserId(Long userId);

  List<NotificationDeviceTokenEntity> findAllByUserIdAndChannel(Long userId, DeviceChannel channel);

}
