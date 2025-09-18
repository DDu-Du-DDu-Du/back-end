package com.ddudu.domain.notification.device.aggregate.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.common.exception.NotificationDeviceTokenErrorCode;
import com.ddudu.fixture.NotificationDeviceTokenFixture;
import java.util.Random;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class DeviceChannelTest {

  int index;

  @BeforeEach
  void setUp() {
    Random random = new Random();
    index = random.nextInt(DeviceChannel.values().length);
  }

  @Test
  void 디바이스_채널_유형_반환을_성공한다() {
    // given
    String expected = DeviceChannel.values()[index]
        .name();

    // when
    DeviceChannel actual = DeviceChannel.get(expected);

    // then
    assertThat(actual.name()).isEqualTo(expected);
  }

  @ParameterizedTest
  @NullSource
  @EmptySource
  void 입력이_빈_값이면_채널_유형_반환을_실패한다(String emptyChannel) {
    // given

    // when
    ThrowingCallable get = () -> DeviceChannel.get(emptyChannel);

    // then
    assertThatIllegalArgumentException().isThrownBy(get)
        .withMessage(NotificationDeviceTokenErrorCode.INVALID_CHANNEL.getCodeName());
  }

  @Test
  void 유효하지_않은_입력이면_채널_유형_반환을_실패한다() {
    // given
    String invalidChannel = NotificationDeviceTokenFixture.getRandomSentence(1, 10);

    // when
    ThrowingCallable get = () -> DeviceChannel.get(invalidChannel);

    // then
    assertThatIllegalArgumentException().isThrownBy(get)
        .withMessage(NotificationDeviceTokenErrorCode.INVALID_CHANNEL.getCodeName());
  }

}