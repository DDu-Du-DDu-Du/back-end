package com.ddudu.application.common.port.notification.out;

import com.ddudu.domain.notification.device.aggregate.NotificationDeviceToken;

public interface NotificationDeviceTokenCommandPort {

  NotificationDeviceToken save(NotificationDeviceToken deviceToken);

}
