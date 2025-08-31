package com.ddudu.domain.notification.device.aggregate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationDeviceToken {

  private Long id;
  private Long userId;
  private String channel;
  private String token;

}
