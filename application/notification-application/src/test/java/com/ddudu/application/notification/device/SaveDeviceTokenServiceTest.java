package com.ddudu.application.notification.device;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.common.dto.notification.request.SaveDeviceTokenRequest;
import com.ddudu.application.common.dto.notification.response.SaveDeviceTokenResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.notification.in.SaveDeviceTokenUseCase;
import com.ddudu.common.exception.NotificationDeviceTokenErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.NotificationDeviceTokenFixture;
import com.ddudu.fixture.UserFixture;
import com.ddudu.infra.mysql.notification.device.repository.NotificationDeviceTokenRepository;
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
  NotificationDeviceTokenRepository notificationDeviceTokenRepository;

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
    SaveDeviceTokenResponse response = saveDeviceTokenUseCase.save(user.getId(), request);

    // then
    assertThat(response.id()).isNotNull();
    // TODO: add actual data after implementing loader port
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

}
