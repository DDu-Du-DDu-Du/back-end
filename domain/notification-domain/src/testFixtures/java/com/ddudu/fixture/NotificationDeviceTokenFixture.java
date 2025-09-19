package com.ddudu.fixture;

import com.ddudu.domain.notification.device.aggregate.NotificationDeviceToken;
import com.ddudu.domain.notification.device.aggregate.enums.DeviceChannel;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NotificationDeviceTokenFixture extends BaseFixture {

  private static final int MIN_CHANNEL_LENGTH = 1;
  private static final int MAX_CHANNEL_LENGTH = 16;
  private static final int MIN_TOKEN_LENGTH = 1;
  private static final int MAX_TOKEN_LENGTH = 512;

  public static NotificationDeviceToken.NotificationDeviceTokenBuilder builder() {
    return builderWith(getRandomUserId(), getRandomChannel(), getRandomToken());
  }

  public static NotificationDeviceToken.NotificationDeviceTokenBuilder builderWith(
      Long userId,
      DeviceChannel channel,
      String token
  ) {
    return NotificationDeviceToken.builder()
        .id(getRandomId())
        .userId(userId)
        .channel(channel)
        .token(token);
  }

  public static NotificationDeviceToken create() {
    return builder().build();
  }

  public static NotificationDeviceToken createWithUser(Long userId) {
    return NotificationDeviceToken.builder()
        .id(null)
        .userId(userId)
        .channel(getRandomChannel())
        .token(getRandomToken())
        .build();
  }

  public static Long getRandomUserId() {
    return getRandomId();
  }

  public static DeviceChannel getRandomChannel() {
    Random random = new Random();
    int index = random.nextInt(DeviceChannel.values().length);

    return DeviceChannel.values()[index];
  }

  public static String getRandomChannelExceedingLimit() {
    return faker.lorem()
        .characters(MAX_CHANNEL_LENGTH + 1, MAX_CHANNEL_LENGTH + 10);
  }

  public static String getRandomToken() {
    return faker.lorem()
        .characters(MIN_TOKEN_LENGTH, MAX_TOKEN_LENGTH);
  }

  public static String getRandomTokenExceedingLimit() {
    return faker.lorem()
        .characters(MAX_TOKEN_LENGTH + 1, MAX_TOKEN_LENGTH + 100);
  }

}
