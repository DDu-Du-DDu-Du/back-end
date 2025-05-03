package com.ddudu.application.service.auth;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.user.auth.service.TokenRefreshService;
import com.ddudu.domain.user.auth.aggregate.RefreshToken;
import com.ddudu.domain.user.auth.aggregate.vo.UserFamily;
import com.ddudu.domain.user.auth.exception.AuthErrorCode;
import com.ddudu.domain.user.auth.service.AuthDomainService;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.user.auth.dto.request.TokenRefreshRequest;
import com.ddudu.application.user.auth.dto.response.TokenResponse;
import com.ddudu.application.user.auth.port.out.SignUpPort;
import com.ddudu.application.user.auth.port.out.TokenLoaderPort;
import com.ddudu.application.user.auth.port.out.TokenManipulationPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.MissingResourceException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class TokenRefreshServiceTest {

  @Autowired
  TokenRefreshService tokenRefreshService;

  @Autowired
  TokenLoaderPort tokenLoaderPort;

  @Autowired
  TokenManipulationPort tokenManipulationPort;

  @Autowired
  AuthDomainService authDomainService;

  @Autowired
  UserLoaderPort userLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  User user;
  int family;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    family = UserFixture.getRandomPositive();
  }

  @Test
  void 토큰_갱신을_성공한다() {
    // given
    RefreshToken refreshToken = authDomainService.createRefreshToken(user, family);

    tokenManipulationPort.save(refreshToken);

    TokenRefreshRequest request = new TokenRefreshRequest(refreshToken.getTokenValue());

    // when
    TokenResponse actual = tokenRefreshService.refresh(request);

    // then
    UserFamily decoded = authDomainService.decodeRefreshToken(actual.refreshToken());
    List<RefreshToken> refreshTokens = tokenLoaderPort.loadByUserFamily(
        decoded.getUserId(), decoded.getFamily());

    assertThat(refreshTokens).anyMatch(token -> token.hasSameTokenValue(actual.refreshToken()));
  }

  @Test
  void 저장된_토큰이_없으면_토큰_갱신을_실패한다() {
    // given
    RefreshToken refreshToken = authDomainService.createRefreshToken(user, family);
    TokenRefreshRequest request = new TokenRefreshRequest(refreshToken.getTokenValue());

    // when
    ThrowingCallable refresh = () -> tokenRefreshService.refresh(request);

    // then
    assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(refresh)
        .withMessage(AuthErrorCode.REFRESH_NOT_ALLOWED.getCodeName());
  }

  @Test
  void 이미_사용한_적이_있는_토큰이라면_갱신을_실패하고_같은_사용자와_패밀리의_토큰을_모두_삭제한다() throws InterruptedException {
    // given
    RefreshToken oldRefreshToken = authDomainService.createRefreshToken(user, family);

    tokenManipulationPort.save(oldRefreshToken);

    sleep(1000);

    RefreshToken newRefreshToken = authDomainService.createRefreshToken(user, family);

    tokenManipulationPort.save(newRefreshToken);

    TokenRefreshRequest request = new TokenRefreshRequest(oldRefreshToken.getTokenValue());

    // when
    ThrowingCallable refresh = () -> tokenRefreshService.refresh(request);

    // then
    assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(refresh)
        .withMessage(AuthErrorCode.REFRESH_NOT_ALLOWED.getCodeName());

    List<RefreshToken> refreshTokens = tokenLoaderPort.loadByUserFamily(
        oldRefreshToken.getUserId(), oldRefreshToken.getFamily());

    assertThat(refreshTokens).isEmpty();
  }

  @Test
  void 토큰의_사용자를_찾을_수_없으면_토큰_갱신을_실패한다() {
    // given
    User wrongUser = UserFixture.createRandomUserWithId();
    RefreshToken refreshToken = authDomainService.createRefreshToken(wrongUser, family);

    tokenManipulationPort.save(refreshToken);

    TokenRefreshRequest request = new TokenRefreshRequest(refreshToken.getTokenValue());

    // when
    ThrowingCallable refresh = () -> tokenRefreshService.refresh(request);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(refresh)
        .withMessage(AuthErrorCode.USER_NOT_FOUND.getCodeName());
  }

}
