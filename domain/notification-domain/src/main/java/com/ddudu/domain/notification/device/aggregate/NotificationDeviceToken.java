package com.ddudu.domain.notification.device.aggregate;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.common.exception.NotificationDeviceTokenErrorCode;
import com.ddudu.domain.notification.device.aggregate.enums.DeviceChannel;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationDeviceToken {

  private static final int MAX_TOKEN_LENGTH = 512;

  @EqualsAndHashCode.Include
  private final Long id;
  private final Long userId;
  private final DeviceChannel channel;
  private final String token;

  @Builder
  private NotificationDeviceToken(Long id, Long userId, DeviceChannel channel, String token) {
    validate(userId, token);

    this.id = id;
    this.userId = userId;
    this.channel = channel;
    this.token = token;
  }

  private void validate(Long userId, String token) {
    checkArgument(
        Objects.nonNull(userId),
        NotificationDeviceTokenErrorCode.NULL_USER_ID.getCodeName()
    );
    validateToken(token);
  }

  private void validateToken(String token) {
    checkArgument(
        StringUtils.isNotBlank(token),
        NotificationDeviceTokenErrorCode.NULL_TOKEN.getCodeName()
    );
    checkArgument(
        token.length() <= MAX_TOKEN_LENGTH,
        NotificationDeviceTokenErrorCode.EXCESSIVE_TOKEN_LENGTH.getCodeName()
    );
  }

}
