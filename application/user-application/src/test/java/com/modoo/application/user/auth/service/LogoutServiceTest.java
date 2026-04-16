package com.modoo.application.user.auth.service;

import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.auth.out.TokenLoaderPort;
import com.modoo.application.common.port.auth.out.TokenManipulationPort;
import com.modoo.application.user.auth.jwt.TokenManager;
import com.modoo.common.exception.AuthErrorCode;
import com.modoo.domain.user.auth.aggregate.RefreshToken;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.UserFixture;
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
class LogoutServiceTest {

  @Autowired
  LogoutService logoutService;

  @Autowired
  TokenManager tokenManager;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  TokenManipulationPort tokenManipulationPort;

  @Autowired
  TokenLoaderPort tokenLoaderPort;

  User loginUser;
  int family;

  @BeforeEach
  void setUp() {
    loginUser = signUpPort.save(UserFixture.createRandomUserWithId());
    family = UserFixture.getRandomPositive();
  }

  @Test
  void 로그아웃을_성공하면_리프레시_토큰_패밀리를_삭제한다() {
    // given
    RefreshToken refreshToken = tokenManager.createRefreshToken(loginUser, family);

    tokenManipulationPort.save(refreshToken);

    // when
    logoutService.logout(loginUser.getId(), refreshToken.getTokenValue());

    // then
    List<RefreshToken> tokens = tokenLoaderPort.loadByUserFamily(loginUser.getId(), family);

    Assertions.assertThat(tokens)
        .isEmpty();
  }

  @Test
  void 로그인_사용자가_존재하지_않으면_로그아웃을_실패한다() {
    // given
    User tokenOwner = signUpPort.save(UserFixture.createRandomUserWithId());
    RefreshToken refreshToken = tokenManager.createRefreshToken(tokenOwner, family);

    tokenManipulationPort.save(refreshToken);

    Long unknownUserId = UserFixture.getRandomLong(1000000000L, Long.MAX_VALUE);

    // when
    ThrowingCallable logout = () ->
        logoutService.logout(unknownUserId, refreshToken.getTokenValue());

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(logout)
        .withMessage(AuthErrorCode.USER_NOT_FOUND.getCodeName());
  }

  @Test
  void 로그인_사용자와_리프레시_토큰_사용자가_다르면_로그아웃을_실패한다() {
    // given
    User tokenOwner = signUpPort.save(UserFixture.createRandomUserWithId());
    RefreshToken refreshToken = tokenManager.createRefreshToken(tokenOwner, family);

    tokenManipulationPort.save(refreshToken);

    // when
    ThrowingCallable logout = () ->
        logoutService.logout(loginUser.getId(), refreshToken.getTokenValue());

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(logout)
        .withMessage(AuthErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
