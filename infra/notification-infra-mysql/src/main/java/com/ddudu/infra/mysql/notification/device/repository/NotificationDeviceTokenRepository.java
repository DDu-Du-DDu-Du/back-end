package com.ddudu.infra.mysql.notification.device.repository;

import com.ddudu.infra.mysql.notification.device.entity.NotificationDeviceTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationDeviceTokenRepository extends
    JpaRepository<NotificationDeviceTokenEntity, Long> {

}
