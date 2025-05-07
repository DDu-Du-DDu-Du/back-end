package com.ddudu.application.user.auth.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.application.dto.auth.request.TokenRefreshRequest;
import com.ddudu.application.dto.auth.response.TokenResponse;
import com.ddudu.application.port.auth.in.TokenRefreshUseCase;
import com.ddudu.application.port.auth.out.TokenLoaderPort;
import com.ddudu.application.port.auth.out.TokenManipulationPort;
import com.ddudu.application.port.user.out.UserLoaderPort;
import com.ddudu.domain.user.auth.aggregate.RefreshToken;
import com.ddudu.domain.user.auth.aggregate.vo.UserFamily;
import com.ddudu.common.exception.AuthErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import java.util.List;
import java.util.MissingResourceException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(noRollbackFor = UnsupportedOperationException.class)
public class TokenRefreshService implements TokenRefreshUseCase {

  private final TokenLoaderPort tokenLoaderPort;
  private final TokenManipulationPort tokenManipulationPort;
  private final TokenManager tokenManager;
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

    String accessToken = tokenManager.createAccessToken(user);
    RefreshToken newRefreshToken = tokenManager.createRefreshToken(
        user, currentRefreshToken.getFamily());

    tokenManipulationPort.save(newRefreshToken);

    return new TokenResponse(accessToken, newRefreshToken.getTokenValue());
  }

  private List<RefreshToken> getTokenFamilyOf(String refreshToken) {
    UserFamily decoded = tokenManager.decodeRefreshToken(refreshToken);

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
