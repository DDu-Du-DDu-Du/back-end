package com.modoo.application.user.auth.service;

import static java.lang.Thread.sleep;

import com.modoo.application.common.dto.auth.request.TokenRefreshRequest;
import com.modoo.application.common.dto.auth.response.TokenResponse;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.auth.out.TokenLoaderPort;
import com.modoo.application.common.port.auth.out.TokenManipulationPort;
import com.modoo.application.user.auth.jwt.TokenManager;
import com.modoo.common.exception.AuthErrorCode;
import com.modoo.domain.user.auth.aggregate.RefreshToken;
import com.modoo.domain.user.auth.aggregate.vo.UserFamily;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.UserFixture;
import java.time.LocalDateTime;
import java.util.List;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
  TokenManager tokenManager;

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
  void 업데이트_성공으로_토큰_갱신을_조기_응답한다() {
    // given
    RefreshToken refreshToken = tokenManager.createRefreshToken(user, family);
    tokenManipulationPort.save(refreshToken);
    TokenRefreshRequest request = new TokenRefreshRequest(refreshToken.getTokenValue());

    // when
    TokenResponse actual = tokenRefreshService.refresh(request);

    // then
    UserFamily decoded = tokenManager.decodeRefreshToken(actual.refreshToken());
    RefreshToken saved = tokenLoaderPort.loadOneByUserFamily(decoded.getUserId(), decoded.getFamily())
        .orElseThrow();

    Assertions.assertThat(saved.getCurrentToken()).isEqualTo(actual.refreshToken());
    Assertions.assertThat(saved.getPreviousToken()).isEqualTo(refreshToken.getTokenValue());
  }

  @Test
  void 업데이트가_실패해도_삼분_이내면_현재_토큰을_응답한다() {
    // given
    RefreshToken oldRefreshToken = tokenManager.createRefreshToken(user, family);
    tokenManipulationPort.save(oldRefreshToken);

    TokenResponse refreshed = tokenRefreshService.refresh(
        new TokenRefreshRequest(oldRefreshToken.getTokenValue())
    );

    TokenRefreshRequest request = new TokenRefreshRequest(oldRefreshToken.getTokenValue());

    // when
    TokenResponse actual = tokenRefreshService.refresh(request);

    // then
    Assertions.assertThat(actual.refreshToken()).isEqualTo(refreshed.refreshToken());
  }

  @Test
  void 리프레시_토큰_디코드에_실패하면_예외를_던진다() {
    // given
    TokenRefreshRequest request = new TokenRefreshRequest("invalid-refresh-token");

    // when
    ThrowingCallable refresh = () -> tokenRefreshService.refresh(request);

    // then
    Assertions.assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(refresh)
        .withMessage(AuthErrorCode.REFRESH_NOT_ALLOWED.getCodeName());
  }

  @Test
  void 사용자_정보가_없으면_토큰_갱신을_실패한다() {
    // given
    User deletedUser = UserFixture.createRandomUserWithId();
    RefreshToken refreshToken = tokenManager.createRefreshToken(deletedUser, family);
    TokenRefreshRequest request = new TokenRefreshRequest(refreshToken.getTokenValue());

    // when
    ThrowingCallable refresh = () -> tokenRefreshService.refresh(request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(refresh)
        .withMessage(AuthErrorCode.USER_NOT_FOUND.getCodeName());
  }

  @Test
  void 사용자와_패밀리로_토큰을_찾지_못하면_토큰_갱신을_실패한다() {
    // given
    RefreshToken refreshToken = tokenManager.createRefreshToken(user, family);
    TokenRefreshRequest request = new TokenRefreshRequest(refreshToken.getTokenValue());

    // when
    ThrowingCallable refresh = () -> tokenRefreshService.refresh(request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(refresh)
        .withMessage(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND.getCodeName());
  }

  @Test
  void 이전_토큰과_다르면_토큰을_삭제하고_토큰_갱신을_실패한다() throws InterruptedException {
    // given
    RefreshToken oldRefreshToken = tokenManager.createRefreshToken(user, family);
    tokenManipulationPort.save(oldRefreshToken);

    tokenRefreshService.refresh(new TokenRefreshRequest(oldRefreshToken.getTokenValue()));

    sleep(1000);
    RefreshToken wrongRefreshToken = tokenManager.createRefreshToken(user, family);
    TokenRefreshRequest request = new TokenRefreshRequest(wrongRefreshToken.getTokenValue());

    // when
    ThrowingCallable refresh = () -> tokenRefreshService.refresh(request);

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(refresh)
        .withMessage(AuthErrorCode.INVALID_AUTHORITY.getCodeName());

    List<RefreshToken> refreshTokens = tokenLoaderPort.loadByUserFamily(user.getId(), family);
    Assertions.assertThat(refreshTokens).isEmpty();
  }

  @Test
  void 이전_토큰이지만_삼분이_지났으면_토큰을_삭제하고_토큰_갱신을_실패한다() throws InterruptedException {
    // given
    RefreshToken oldRefreshToken = tokenManager.createRefreshToken(user, family);
    tokenManipulationPort.save(oldRefreshToken);

    sleep(1000);
    RefreshToken newRefreshToken = tokenManager.createRefreshToken(user, family);
    Assertions.assertThat(newRefreshToken.getTokenValue()).isNotEqualTo(oldRefreshToken.getTokenValue());
    tokenManipulationPort.rotateIfCurrentMatches(
        user.getId(),
        family,
        oldRefreshToken.getTokenValue(),
        newRefreshToken.getTokenValue(),
        LocalDateTime.now().minusMinutes(4)
    );

    TokenRefreshRequest request = new TokenRefreshRequest(oldRefreshToken.getTokenValue());

    // when
    ThrowingCallable refresh = () -> tokenRefreshService.refresh(request);

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(refresh)
        .withMessage(AuthErrorCode.INVALID_AUTHORITY.getCodeName());

    List<RefreshToken> refreshTokens = tokenLoaderPort.loadByUserFamily(user.getId(), family);
    Assertions.assertThat(refreshTokens).isEmpty();
  }

}
