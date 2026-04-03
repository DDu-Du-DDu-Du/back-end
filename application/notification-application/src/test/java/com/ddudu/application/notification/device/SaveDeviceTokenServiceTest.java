package com.ddudu.application.notification.device;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.notification.request.SaveDeviceTokenRequest;
import com.ddudu.application.common.dto.notification.response.SaveDeviceTokenResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.notification.in.SaveDeviceTokenUseCase;
import com.ddudu.application.common.port.notification.out.NotificationDeviceTokenCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationDeviceTokenLoaderPort;
import com.ddudu.common.exception.NotificationDeviceTokenErrorCode;
import com.ddudu.domain.notification.device.aggregate.NotificationDeviceToken;
import com.ddudu.domain.notification.device.aggregate.enums.DeviceChannel;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.NotificationDeviceTokenFixture;
import com.ddudu.fixture.UserFixture;
import java.util.List;
import java.util.MissingResourceException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
@Transactional
class SaveDeviceTokenServiceTest {

  @Autowired
  SaveDeviceTokenUseCase saveDeviceTokenUseCase;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  NotificationDeviceTokenLoaderPort notificationDeviceTokenLoaderPort;

  @Autowired
  NotificationDeviceTokenCommandPort notificationDeviceTokenCommandPort;

  User user;
  String channel;
  String token;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    channel = NotificationDeviceTokenFixture.getRandomChannel()
        .name();
    token = NotificationDeviceTokenFixture.getRandomToken();
  }

  @Test
  void 디바이스_토큰_저장에_성공한다() {
    // given
    SaveDeviceTokenRequest request = new SaveDeviceTokenRequest(channel, token);

    // when
    SaveDeviceTokenResponse actual = saveDeviceTokenUseCase.save(user.getId(), request);

    // then
    NotificationDeviceToken expected = notificationDeviceTokenLoaderPort.getAllTokensOfUser(
            user.getId()
        )
        .stream()
        .findFirst()
        .orElseThrow();

    assertThat(actual.id()).isEqualTo(expected.getId());
  }

  @Test
  void 로그인_사용자가_없으면_예외를_던진다() {
    // given
    long invalidId = NotificationDeviceTokenFixture.getRandomId();
    SaveDeviceTokenRequest request = new SaveDeviceTokenRequest(channel, token);

    // when
    ThrowingCallable save = () -> saveDeviceTokenUseCase.save(invalidId, request);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(save)
        .withMessage(NotificationDeviceTokenErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 동일_사용자_동일_플랫폼에_동일_토큰이_존재하면_저장을_생략한다() {
    // given
    DeviceChannel existingChannel = NotificationDeviceTokenFixture.getRandomChannel();
    String existingToken = NotificationDeviceTokenFixture.getRandomToken();
    NotificationDeviceToken existing = notificationDeviceTokenCommandPort.save(
        NotificationDeviceTokenFixture.builderWith(user.getId(), existingChannel, existingToken)
            .id(null)
            .build()
    );
    SaveDeviceTokenRequest request = new SaveDeviceTokenRequest(existingChannel.name(), existingToken);

    // when
    SaveDeviceTokenResponse actual = saveDeviceTokenUseCase.save(user.getId(), request);

    // then
    List<NotificationDeviceToken> tokens = notificationDeviceTokenLoaderPort.getAllTokensOfUser(user.getId());

    assertThat(actual.id()).isEqualTo(existing.getId());
    assertThat(tokens).hasSize(1);
  }

  @Test
  void 동일_사용자_동일_플랫폼에_다른_토큰이면_새로_저장한다() {
    // given
    DeviceChannel existingChannel = NotificationDeviceTokenFixture.getRandomChannel();
    String existingToken = NotificationDeviceTokenFixture.getRandomToken();
    notificationDeviceTokenCommandPort.save(
        NotificationDeviceTokenFixture.builderWith(user.getId(), existingChannel, existingToken)
            .id(null)
            .build()
    );
    String newToken = NotificationDeviceTokenFixture.getRandomToken();
    SaveDeviceTokenRequest request = new SaveDeviceTokenRequest(existingChannel.name(), newToken);

    // when
    SaveDeviceTokenResponse actual = saveDeviceTokenUseCase.save(user.getId(), request);

    // then
    List<NotificationDeviceToken> tokens = notificationDeviceTokenLoaderPort.getAllTokensOfUser(user.getId());

    assertThat(tokens).hasSize(2);
    assertThat(tokens).extracting(NotificationDeviceToken::getId)
        .contains(actual.id());
    assertThat(tokens).extracting(NotificationDeviceToken::getToken)
        .contains(existingToken, newToken);
  }

}
