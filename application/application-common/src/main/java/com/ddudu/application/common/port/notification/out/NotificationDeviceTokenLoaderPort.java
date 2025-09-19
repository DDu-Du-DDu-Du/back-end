package com.ddudu.application.common.port.notification.out;

import com.ddudu.domain.notification.device.aggregate.NotificationDeviceToken;
import java.util.List;

public interface NotificationDeviceTokenLoaderPort {

  List<NotificationDeviceToken> getAllTokensOfUser(Long userId);

}
