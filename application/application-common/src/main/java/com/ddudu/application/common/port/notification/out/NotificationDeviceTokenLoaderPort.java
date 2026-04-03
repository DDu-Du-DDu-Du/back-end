package com.ddudu.application.common.port.notification.out;

import com.ddudu.domain.notification.device.aggregate.NotificationDeviceToken;
import com.ddudu.domain.notification.device.aggregate.enums.DeviceChannel;
import java.util.List;

public interface NotificationDeviceTokenLoaderPort {

  List<NotificationDeviceToken> getAllTokensOfUser(Long userId);

  List<NotificationDeviceToken> getTokensOfUserByChannel(Long userId, DeviceChannel channel);

}
