package com.modoo.application.common.port.notification.out;

import com.modoo.domain.notification.device.aggregate.NotificationDeviceToken;

public interface NotificationDeviceTokenCommandPort {

  NotificationDeviceToken save(NotificationDeviceToken deviceToken);

}
