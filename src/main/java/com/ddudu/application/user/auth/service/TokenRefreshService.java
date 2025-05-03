package com.ddudu.application.user.auth.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.user.auth.aggregate.RefreshToken;
import com.ddudu.domain.user.auth.aggregate.vo.UserFamily;
import com.ddudu.domain.user.auth.exception.AuthErrorCode;
import com.ddudu.domain.user.auth.service.AuthDomainService;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.user.auth.dto.request.TokenRefreshRequest;
import com.ddudu.application.user.auth.dto.response.TokenResponse;
import com.ddudu.application.user.auth.port.in.TokenRefreshUseCase;
import com.ddudu.application.user.auth.port.out.TokenLoaderPort;
import com.ddudu.application.user.auth.port.out.TokenManipulationPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional(dontRollbackOn = UnsupportedOperationException.class)
public class TokenRefreshService implements TokenRefreshUseCase {

  private final TokenLoaderPort tokenLoaderPort;
  private final TokenManipulationPort tokenManipulationPort;
  private final AuthDomainService authDomainService;
  private final UserLoaderPort userLoaderPort;

  @Override
  public TokenResponse refresh(TokenRefreshRequest request) {
    List<RefreshToken> tokenFamily = getTokenFamilyOf(request.refreshToken());
    RefreshToken currentRefreshToken = tokenFamily.stream()
        .filter(token -> token.hasSameTokenValue(request.refreshToken()))
        .findFirst()
        .orElseThrow(() -> new UnsupportedOperationException(
            AuthErrorCode.REFRESH_NOT_ALLOWED.getCodeName()));

    validateNotUsed(tokenFamily, currentRefreshToken);

    User user = userLoaderPort.loadFullUser(currentRefreshToken.getUserId())
        .orElseThrow(() -> new MissingResourceException(
            AuthErrorCode.USER_NOT_FOUND.getCodeName(),
            User.class.getCanonicalName(),
            String.valueOf(currentRefreshToken.getUserId())
        ));

    String accessToken = authDomainService.createAccessToken(user);
    RefreshToken newRefreshToken = authDomainService.createRefreshToken(
        user, currentRefreshToken.getFamily());

    tokenManipulationPort.save(newRefreshToken);

    return new TokenResponse(accessToken, newRefreshToken.getTokenValue());
  }

  private List<RefreshToken> getTokenFamilyOf(String refreshToken) {
    UserFamily decoded = authDomainService.decodeRefreshToken(refreshToken);

    return tokenLoaderPort.loadByUserFamily(
        decoded.getUserId(), decoded.getFamily());
  }

  private void validateNotUsed(List<RefreshToken> tokenFamily, RefreshToken refreshToken) {
    if (tokenFamily.size() == 1) {
      return;
    }

    Long mostRecent = tokenFamily.stream()
        .map(RefreshToken::getId)
        .findFirst()
        .get();

    if (!refreshToken.hasSameId(mostRecent)) {
      tokenManipulationPort.deleteAllFamily(tokenFamily);

      throw new UnsupportedOperationException(AuthErrorCode.REFRESH_NOT_ALLOWED.getCodeName());
    }
  }

}
