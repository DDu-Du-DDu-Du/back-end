package com.ddudu.application.notification.device;

import com.ddudu.application.common.dto.notification.request.SaveDeviceTokenRequest;
import com.ddudu.application.common.dto.notification.response.SaveDeviceTokenResponse;
import com.ddudu.application.common.port.notification.in.SaveDeviceTokenUseCase;
import com.ddudu.application.common.port.notification.out.NotificationDeviceTokenCommandPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.NotificationDeviceTokenErrorCode;
import com.ddudu.domain.notification.device.aggregate.NotificationDeviceToken;
import com.ddudu.domain.notification.device.aggregate.enums.DeviceChannel;
import com.ddudu.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class SaveDeviceTokenService implements SaveDeviceTokenUseCase {

  private final UserLoaderPort userLoaderPort;
  private final NotificationDeviceTokenCommandPort notificationDeviceTokenCommandPort;

  @Override
  public SaveDeviceTokenResponse save(Long loginId, SaveDeviceTokenRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        NotificationDeviceTokenErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    DeviceChannel channel = DeviceChannel.get(request.channel());
    NotificationDeviceToken deviceToken = NotificationDeviceToken.builder()
        .userId(user.getId())
        .channel(channel)
        .token(request.token())
        .build();
    NotificationDeviceToken saved = notificationDeviceTokenCommandPort.save(deviceToken);

    return new SaveDeviceTokenResponse(saved.getId());
  }

}
