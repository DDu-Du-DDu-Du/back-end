package com.modoo.domain.notification.device.aggregate.enums;

import com.modoo.common.exception.NotificationDeviceTokenErrorCode;
import java.util.Arrays;
import java.util.Objects;

public enum DeviceChannel {
  MOBILE,
  WEB,
  PC;

  public static DeviceChannel get(String channel) {
    return Arrays.stream(DeviceChannel.values())
        .filter(deviceChannel -> Objects.equals(deviceChannel.name(), channel))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(
                NotificationDeviceTokenErrorCode.INVALID_CHANNEL.getCodeName()
            )
        );
  }
}
