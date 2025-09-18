package com.ddudu.domain.notification.device.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.ddudu.common.exception.NotificationDeviceTokenErrorCode;
import com.ddudu.domain.notification.device.aggregate.NotificationDeviceToken.NotificationDeviceTokenBuilder;
import com.ddudu.domain.notification.device.aggregate.enums.DeviceChannel;
import com.ddudu.fixture.NotificationDeviceTokenFixture;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class NotificationDeviceTokenTest {

  Long userId;
  DeviceChannel channel;
  String token;

  @BeforeEach
  void setUp() {
    userId = NotificationDeviceTokenFixture.getRandomUserId();
    channel = NotificationDeviceTokenFixture.getRandomChannel();
    token = NotificationDeviceTokenFixture.getRandomToken();
  }

  @Test
  void 디바이스_토큰을_생성한다() {
    // given
    NotificationDeviceTokenBuilder builder = NotificationDeviceTokenFixture.builderWith(
        userId,
        channel,
        token
    );

    // when
    NotificationDeviceToken actual = builder.build();

    // then
    assertThat(actual.getUserId()).isEqualTo(userId);
    assertThat(actual.getChannel()).isEqualTo(channel);
    assertThat(actual.getToken()).isEqualTo(token);
  }

  @Test
  void 수신자가_없으면_예외가_발생한다() {
    // given
    NotificationDeviceTokenBuilder invalidBuilder = NotificationDeviceTokenFixture.builderWith(
        null,
        channel,
        token
    );

    // when
    ThrowingCallable create = invalidBuilder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(create)
        .withMessage(NotificationDeviceTokenErrorCode.NULL_USER_ID.getCodeName());
  }

  @Test
  void 채널이_없으면_예외가_발생한다() {
    // given
    NotificationDeviceTokenBuilder invalidBuilder = NotificationDeviceTokenFixture.builderWith(
        userId,
        null,
        token
    );

    // when
    ThrowingCallable create = invalidBuilder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(create)
        .withMessage(NotificationDeviceTokenErrorCode.INVALID_CHANNEL.getCodeName());
  }

  @Test
  void 토큰이_없으면_예외가_발생한다() {
    // given
    NotificationDeviceTokenBuilder invalidBuilder = NotificationDeviceTokenFixture.builderWith(
        userId,
        channel,
        null
    );

    // when
    ThrowingCallable create = invalidBuilder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(create)
        .withMessage(NotificationDeviceTokenErrorCode.NULL_TOKEN.getCodeName());
  }

  @Test
  void 토큰이_공백이면_예외가_발생한다() {
    // given
    NotificationDeviceTokenBuilder invalidBuilder = NotificationDeviceTokenFixture.builderWith(
        userId,
        channel,
        " "
    );

    // when
    ThrowingCallable create = invalidBuilder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(create)
        .withMessage(NotificationDeviceTokenErrorCode.NULL_TOKEN.getCodeName());
  }

  @Test
  void 토큰이_512자를_초과하면_예외가_발생한다() {
    // given
    String longToken = NotificationDeviceTokenFixture.getRandomTokenExceedingLimit();
    NotificationDeviceTokenBuilder invalidBuilder = NotificationDeviceTokenFixture.builderWith(
        userId,
        channel,
        longToken
    );

    // when
    ThrowingCallable create = invalidBuilder::build;

    // then
    assertThatIllegalArgumentException().isThrownBy(create)
        .withMessage(NotificationDeviceTokenErrorCode.EXCESSIVE_TOKEN_LENGTH.getCodeName());
  }
}