package com.ddudu.application.service.auth;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.authentication.domain.RefreshToken;
import com.ddudu.application.domain.authentication.domain.vo.UserFamily;
import com.ddudu.application.domain.authentication.dto.request.TokenRefreshRequest;
import com.ddudu.application.domain.authentication.dto.response.TokenResponse;
import com.ddudu.application.domain.authentication.exception.AuthErrorCode;
import com.ddudu.application.domain.authentication.service.AuthDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.auth.TokenRefreshUseCase;
import com.ddudu.application.port.out.auth.TokenLoaderPort;
import com.ddudu.application.port.out.auth.TokenManipulationPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
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
